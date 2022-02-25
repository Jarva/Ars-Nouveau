package com.hollingsworth.arsnouveau.api.sound;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public class ConfiguredSpellSound {
    public static ConfiguredSpellSound EMPTY = new ConfiguredSpellSound(null);

    public @Nullable SpellSound sound;
    public float volume;
    public float pitch;

    public ConfiguredSpellSound(@Nullable SpellSound sound) {
        this(sound, 1, 1);
    }

    public ConfiguredSpellSound(@Nullable SpellSound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();
        tag.put("soundTag", sound == null ? new CompoundTag() : sound.serialize());
        tag.putFloat("volume", volume);
        tag.putFloat("pitch", pitch);
        return tag;
    }

    public static ConfiguredSpellSound fromTag(CompoundTag tag){
        SpellSound sound = SpellSound.fromTag(tag.getCompound("soundTag"));
        float volume = tag.getFloat("volume");
        float pitch = tag.getFloat("pitch");
        return new ConfiguredSpellSound(sound, volume, pitch);
    }
}