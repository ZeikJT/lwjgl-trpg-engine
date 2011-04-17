Press spacebar to active a point-light. Again to deactivate.
WASD moves the sprite and the camera focus around.
Q Rotates the camera CCW, E rotates the camera CW
The Enter button toggles an XYZ axis

The code is ugly, but it's all in good fun and I'm learning a lot.

Version 1.41
------------
 + SpriteSheet class added, extends Sprite
 + Cursor sprite updated to rotate based on camera

Version 1.4
------------
 + Billboard and Point Sprite classes consolidated to Sprite
 + Sprite class and Model class now re-use resources if possible
 + Sprites are now depth sorted
   Possibly over-intensive sorting algorithm
 + Camera workings fixed up to allow for easier depth sorting
 * Map loading planned as next feature
 * Sprite Sheet is also planned

Version 1.3
------------
 + Billboard sprite class added.
   These sprites can be scaled on independant axes.
   They can also be part of a sprite sheet with texture mapping.
 * I found out that depth sorting is particularly important for
   transparent textures. Need to fix this ASAP.

Version 1.2
------------
 + Point sprite class added.
   Now sprites can be created that always face the camera.
   Sprite is unaffected by light.
 + Loaded custom models can now be textured and scaled.
 + Terrain loaded through model and scaled to random heights.


Version 1.1
------------
 + Added model loader with custom .obj model type.
   Now dynamic model loading is possible.


Version 1.0
------------
 + A small TRPG engine test.