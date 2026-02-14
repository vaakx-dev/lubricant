package wd40.lubricant.api;

// Marker interface for mod content classes. Lubricant force-loads every implementation
// via JDK ServiceLoader at the right loader lifecycle moment, so the implementing
// class's static fields run (and ItemRegistry.create + register calls land in queues).
//
// Implementations need a public no-arg constructor.
//
// Mod authors register their content classes in:
//   META-INF/services/wd40.lubricant.api.Init
//
// Example services file with multiple content classes:
//   com.example.cog.CogItems
//   com.example.cog.CogBlocks
//
public interface Init {
}
