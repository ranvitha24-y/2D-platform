# REALITY SHIFT: BEYOND THE RIFT

A five-world LibGDX side-scrolling adventure. Gameplay is 2D and uses `SpriteBatch`, textures, an orthographic camera, platform collision, and shared `GameCommand` input. The existing LWJGL3 launcher remains the desktop entry point.

## Run from VS Code

1. Open this project folder in VS Code with a Java extension pack installed.
2. Open the integrated terminal at the project root.
3. Run `gradlew.bat lwjgl3:run` on Windows. On macOS/Linux, run `./gradlew lwjgl3:run`.
4. Build all configured targets with `gradlew.bat build` or `./gradlew build`.

## Controls

- Move: A/D or left/right arrows
- Jump: Space, W, or up arrow
- Attack: J or F
- Select Fire, Water, Earth, Wind: 1, 2, 3, 4
- Interact: E
- Pause: P or Escape
- Continue through a portal/story card: Enter, Space, or a voice/gesture `CONFIRM`
- Restart current world: R

The game exposes `Main.getVoiceCommandAdapter().accept(phrase)` for a speech recognizer and `Main.getGestureCommandAdapter().onGesture(GameCommand, active)` for an external gesture recognizer. Voice and OpenCV recognition are not included. Gesture movement should send both activation and release events. Both adapters submit the same `GameCommand` values used by keyboard input.

## Story and gameplay

The Prologue introduces Zarvok's failed experiment, the shattered Heart, the prophecy, and the traveller. The five connected worlds are Enchanted Jungle, Lost Kingdom of Agrabah, Academy of Arcane, Real World, and Null Realm. Each world has collectible objectives, a themed environment, elemental puzzles, an enemy encounter, a boss, and a portal transition. The final boss requires attacks with all four distinct elements before the Heart can be restored.

Placeholder pixel-art textures are generated in code so the game does not need external sprite files to launch. Replace them with artist-made sprite sheets later, keeping the gameplay and shared command interfaces in place.

## 3D video cutscenes

The cutscene screen uses the official `gdx-video` extension, added as version `1.3.4` for the current LibGDX `1.14.2` project. LibGDX itself does not natively play MP4. The standard desktop gdx-video codec set supports WebM with VP8 video and Vorbis audio; H.264 MP4 is not supported by the standard desktop build. Adding codecs requires custom FFmpeg/native builds. See the [gdx-video README](https://github.com/libgdx/gdx-video/blob/master/README.md).

Encode cutscenes as `.webm` (VP8 + Vorbis), preferably 1280x720 at 24 or 30 fps, and place them in `assets/videos/`. The game plays a clip when present; otherwise it displays the narration card and continues after 12 seconds. Enter, Space, Escape, click, or any submitted game command skips a clip/card. `CutsceneScreen` disposes decoder resources on exit.

Expected optional clips:

- `prologue.webm`
- `level1-intro.webm` through `level5-intro.webm`
- `level1-boss.webm` through `level5-boss.webm`
- `level1-transition.webm` through `level4-transition.webm`
- `ending.webm`

These filenames are referenced by `CutsceneData` and `Main`. No video files are included yet, so actual video decoding has not been exercised. The desktop dependency is in `lwjgl3/build.gradle`; the shared player API and version are in `core/build.gradle` and `gradle.properties` respectively. The launcher class remains unchanged.

## Project map

- `Main.java`: shared input routing, story transitions, and persistent progress.
- `StoryLevel.java`, `GameProgress.java`: world definitions and cross-level story progress.
- `AdventureScreen.java`: shared 2D movement, collision, platforms, puzzles, enemies, boss fights, and HUD.
- `Level1Screen.java`: named entry point for the Enchanted Jungle.
- `Player.java`, `Element.java`, `PixelArt.java`: player state, powers, and generated sprite assets.
- `CutsceneData.java`, `CutsceneScreen.java`, `EndingScreen.java`: optional video playback and story cards.
- `input/GameCommand.java`, `CommandSink.java`, `VoiceCommandAdapter.java`, `GestureCommandAdapter.java`: shared control integration.

## Character animations and teammate integration

`Player` now exposes a timed animation state; `AdventureScreen` selects idle, walk/run, jump/land/fall, attack, elemental, damage, interaction, and victory states. `CharacterAnimations` accepts horizontal sprite strips per character and state. `AnimationEventListener` provides story/gameplay events to the visual-effects teammate. `LevelEffects` supplies simple generated environmental motion for each world until authored effects arrive. Integration instructions and the registration example are in [docs/ANIMATION_INTEGRATION.md](docs/ANIMATION_INTEGRATION.md).

The current project contains no authored character sheets or cinematic files. Character states therefore use the generated fallback sprite until the animation teammate's strips are registered. Procedural environment effects are temporary visual stand-ins. The Java build verifies compilation; inspect the running game after registering the art to confirm frame selection and final visual timing.

The five levels compile and the desktop launcher starts. Voice recognition and OpenCV recognition require the external modules to be connected. Runtime progression through all level objectives and playback with supplied video files still need hands-on testing.
