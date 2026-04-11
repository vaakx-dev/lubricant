package wd40.lubricant.api.datagen;

/**
 * Top-level datagen handle passed to {@link DatagenInit#onDatagen}. Create a
 * {@link Pack}, attach providers to it, and lubricant will run each provider
 * against a shared {@link DataOutput} sink before flushing to disk.
 */
public interface DataGenerator {

    /** Create a fresh pack to attach providers to. */
    Pack createPack();
}
