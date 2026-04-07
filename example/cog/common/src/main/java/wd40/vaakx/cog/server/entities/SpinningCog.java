package wd40.vaakx.cog.server.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * A do-nothing entity used to demonstrate {@link wd40.lubricant.api.data.Entities}.
 *
 * <p>No renderer is registered; the entity is invisible at runtime. Spawn one
 * with {@code /summon cog:spinning_cog}, attach data with
 * {@code Entities.set(...)}, observe persistence across save+reload.</p>
 */
public final class SpinningCog extends Entity {

    public SpinningCog(EntityType<? extends SpinningCog> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
