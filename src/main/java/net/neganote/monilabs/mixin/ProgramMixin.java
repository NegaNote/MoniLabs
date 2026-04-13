package net.neganote.monilabs.mixin;

import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.client.render.ShaderAnalysisResult;

import com.mojang.blaze3d.shaders.Program;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(Program.class)
public class ProgramMixin {

    @Unique
    private static final Pattern MAIN_PATTERN = Pattern.compile("void\\s+main\\s*\\(\\s*\\)\\s*\\{([\\s\\S]*)}");
    @Unique
    private static final Pattern DECL_PATTERN = Pattern
            .compile("\\b((?:[iu]?vec\\d|float|int|bool))\\b\\s+([\\w\\d_]+)");
    @Unique
    private static final Pattern TARGET_PATTERN = Pattern
            .compile("layout\\s*\\(\\s*location\\s*=\\s*0\\s*\\)\\s*out\\s+vec4\\s+(\\w+)");
    @Unique
    private static final Pattern VAR_PATTERN = Pattern.compile("\\b([a-zA-Z_]\\w*)\\b");

    // Works for all popular shaders
    // Basically what this does is find the last write to the color variable before it gets actually written to the
    // framebuffer
    @Unique
    private static ShaderAnalysisResult moniLabs$analyzeShaderForColorWrite(String content) {
        Matcher mainMatcher = MAIN_PATTERN.matcher(content);
        if (!mainMatcher.find()) return new ShaderAnalysisResult("", "", false);

        String mainBody = mainMatcher.group(1);

        Matcher targetMatcher = TARGET_PATTERN.matcher(content);
        String targetOut = targetMatcher.find() ? targetMatcher.group(1) : "gl_FragColor";

        Map<String, String> knownTypes = new HashMap<>();
        String[] lines = mainBody.split("\n");

        for (String line : lines) {
            line = line.trim().replaceAll("//.*", "");
            if (line.isEmpty()) {
                continue;
            }

            Matcher declMatch = DECL_PATTERN.matcher(line);
            while (declMatch.find()) {
                knownTypes.put(declMatch.group(2), declMatch.group(1));
            }

            if (line.contains(targetOut) && line.contains("=")) {
                String rightSide = line.substring(line.indexOf('=') + 1);
                Matcher varMatch = VAR_PATTERN.matcher(rightSide);

                while (varMatch.find()) {
                    String candidate = varMatch.group(1);
                    if (Character.isDigit(candidate.charAt(0))) continue;

                    if (knownTypes.containsKey(candidate)) {
                        return new ShaderAnalysisResult(line, candidate, true);
                    }
                }
            }
        }
        return new ShaderAnalysisResult("", "", false);
    }

    @Unique
    private static String moniLabs$modifyShader(String code, ShaderAnalysisResult result) {
        if (!result.valid()) {
            MoniLabs.LOGGER.log(Level.ERROR,
                    "Could not successfully analyze current shader for colored ender portal inject");
            return code;
        }
        String newSource = code.replace("void main", """
                vec3 moni_rgb2hsv(vec3 c) {
                    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
                    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
                    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

                    float d = q.x - min(q.w, q.y);
                    float e = 1.0e-10;
                    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
                }
                vec3 moni_hsv2rgb(vec3 c) {
                    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
                    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
                    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
                }
                void main""");

        newSource = newSource.replace(result.firstWriteLine(), """
                if (iris_entityInfo.x == 6767) {
                vec3 _origHsv = moni_rgb2hsv(%s.rgb);
                vec3 _destHsv = moni_rgb2hsv(iris_vertexColor.rgb);
                //copy hue and saturation
                _origHsv.x = _destHsv.x;
                _origHsv.y = _destHsv.y;
                %s.rgb = moni_hsv2rgb(_origHsv);
                }
                """.formatted(result.referencedVariable(), result.referencedVariable()) + "\n\t" +
                result.firstWriteLine());
        return newSource;
    }

    @Redirect(method = "compileShaderInternal",
              at = @At(value = "INVOKE",
                       target = "Lorg/apache/commons/io/IOUtils;toString(Ljava/io/InputStream;Ljava/nio/charset/Charset;)Ljava/lang/String;"))
    private static String moniLabs$modifyIrisEntityDiffuseShader(InputStream sw, Charset input, Program.Type type,
                                                                 String name) throws IOException {
        String original = IOUtils.toString(sw, input);
        if (name.contains("block_entity_diffuse") && type == Program.Type.FRAGMENT) {
            var analysisResult = moniLabs$analyzeShaderForColorWrite(original);
            return moniLabs$modifyShader(original, analysisResult);
        }

        return original;
    }
}
