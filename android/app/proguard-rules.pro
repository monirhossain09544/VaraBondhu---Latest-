# Preserve runtime metadata used by serialization and networking libraries.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault

# Mapbox SDK dependencies publish their own consumer rules. Keep JNI entry points
# that may be resolved by native code after R8 renaming.
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
