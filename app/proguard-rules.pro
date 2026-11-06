# R8 rules for Habits. Compose, Lifecycle, Navigation, Room, Glance, WorkManager and
# kotlinx.coroutines ship their own consumer rules; this file adds only what they leave out.
# The app itself uses no serialization, JNI or reflection of its own.

# Keep file names and line numbers so Play Console can retrace crashes with the mapping file
# that the release bundle carries.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room creates AppDatabase_Impl (and WorkManager's WorkDatabase_Impl) by reflection through the
# no-argument constructor. Room 2.6.1's consumer rule keeps the class only, and R8 full mode does
# not keep that constructor implicitly.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
