package net.neganote.monilabs.saveddata;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// Copied from CosmicCore with some minor changes (thank you Caitlynn!)
public class UniqueMultiblockData {

    @Getter
    public static class UniqueMultiblockId {

        private final String multiblockType;

        protected UniqueMultiblockId(String multiblockType) {
            this.multiblockType = multiblockType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UniqueMultiblockId that = (UniqueMultiblockId) o;
            return Objects.equals(multiblockType, that.multiblockType);
        }

        @Override
        public int hashCode() {
            int result = 17; // Some arbitrary prime number
            result = 31 * result + multiblockType.hashCode();
            return result;
        }
    }

    private static final String MULTIBLOCK_TYPE = "multiblockType";
    private static final String MULTIBLOCK_POS = "multiblockPos";

    // Map a tuple of "Multiblock Type" and "Dimension Name" to a "BlockPos"
    public Map<UniqueMultiblockId, BlockPos> data;

    public UniqueMultiblockData() {
        this.data = new HashMap<>();
    }

    public static UniqueMultiblockData fromTag(ListTag tag) {
        var result = new UniqueMultiblockData();
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag entry = tag.getCompound(i);
            var type = entry.getString(MULTIBLOCK_TYPE);
            var pos = BlockPos.of(entry.getLong(MULTIBLOCK_POS));
            result.data.put(new UniqueMultiblockId(type), pos);
        }
        return result;
    }

    public ListTag toTag() {
        var uniqueMultiblockData = new ListTag();
        for (var entry : data.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;
            var entryTag = new CompoundTag();
            entryTag.putString(MULTIBLOCK_TYPE, entry.getKey().getMultiblockType());
            entryTag.putLong(MULTIBLOCK_POS, entry.getValue().asLong());
            uniqueMultiblockData.add(entryTag);
        }
        return uniqueMultiblockData;
    }

    public boolean hasData(String multiblockType) {
        return data.containsKey(new UniqueMultiblockId(multiblockType));
    }

    public boolean isUnique(String multiblockType, BlockPos pos) {
        var key = new UniqueMultiblockId(multiblockType);
        if (!data.containsKey(key)) return true;
        else return data.get(key).equals(pos);
    }

    public void addMultiblock(String multiblockType, BlockPos pos) {
        data.put(new UniqueMultiblockId(multiblockType), pos);
    }

    public void removeMultiblock(String multiblockType, BlockPos pos) {
        var key = new UniqueMultiblockId(multiblockType);
        if (!hasData(multiblockType)) return;
        if (data.get(key).equals(pos)) data.remove(key);
    }
}
