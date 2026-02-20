# Refactoring Summary (Current State)

## Purpose
This refactor sequence was done to address the lecturer feedback about:
- God-object/service-locator behavior in `Game`
- MVC boundary violations (model rendering, views driving state, controller/view coupling)
- Pattern use that added indirection without practical value

The target architecture is model-heavy MVC:
- **Model** = gameplay state + rules
- **View** = rendering + UI interaction surfaces
- **Controller** = input routing, state transitions, side effects (audio/persistence)

---

## Changes Completed (In Order) and Why

## 1) State classes moved from view package to controller package
### What changed
- Added controller state classes:
  - `src/main/java/main/controller/state/GameBaseState.java`
  - `src/main/java/main/controller/state/GamingState.java`
  - `src/main/java/main/controller/state/MenuState.java`
  - `src/main/java/main/controller/state/LeaderboardState.java`
  - `src/main/java/main/controller/state/LevelSelectState.java`
- Removed old `main.view.interfaces.*State` hierarchy.

### Why
These classes are screen/state-machine controllers (transition + routing behavior), so they belong in the controller layer, not in view.

---

## 2) Rendering and image concerns were moved out of model entities/levels
### What changed
Model side (render concerns removed):
- `src/main/java/main/model/entities/entity/Player.java`
- `src/main/java/main/model/entities/entity/Spike.java`
- `src/main/java/main/model/entities/entity/TriggerPlatform.java`
- `src/main/java/main/model/entities/entity/TriggerSpike.java`
- `src/main/java/main/model/entities/entity/SpawnPlatform.java`
- `src/main/java/main/model/entities/entity/MovingPlatform.java`
- `src/main/java/main/model/entities/entity/DeathSprite.java`
- `src/main/java/main/model/entities/entity/Entity.java`
- `src/main/java/main/model/levels/Level.java`
- `src/main/java/main/model/levels/LevelConfigLoader.java`

View side (renderers own drawing/assets):
- `src/main/java/main/view/render/PlayerRenderer.java`
- `src/main/java/main/view/render/LevelRenderer.java`
- `src/main/java/main/view/GameView.java`

### Why
Model should not render itself. Moving drawing into `main.view.render` removes direct model->view coupling and makes model logic testable without graphics APIs.

---

## 3) Views were made callback-driven (passive)
### What changed
Action interfaces are used by views to emit intent:
- `src/main/java/main/view/states/Actions/MainMenuActions.java`
- `src/main/java/main/view/states/Actions/LevelSelectActions.java`
- `src/main/java/main/view/states/Actions/LeaderboardActions.java`

Views now call actions instead of mutating game flow directly:
- `src/main/java/main/view/states/MainMenu.java`
- `src/main/java/main/view/states/LevelSelect.java`
- `src/main/java/main/view/states/Leaderboard.java`

Controller-state classes implement those actions:
- `src/main/java/main/controller/state/MenuState.java`
- `src/main/java/main/controller/state/LevelSelectState.java`
- `src/main/java/main/controller/state/LeaderboardState.java`

### Why
View should not decide transitions (`setGameState`) or persistence logic. It should emit UI intent; controller decides behavior.

---

## 4) Input handling was converted to command-driven invocations
### What changed
- `KeyboardInputs` is now a pure invoker:
  - `keyPressed/keyReleased/keyTyped` dispatch command bindings only.
  - No direct state branches in key handlers.
  - File: `src/main/java/main/controller/inputs/KeyboardInputs.java`
- Command classes under:
  - `src/main/java/main/controller/inputs/commands/*.java`
- Added/used commands for:
  - movement press/release
  - jump press/release
  - pause/menu
  - leaderboard left/right
  - name edit control keys
  - name typed input
  - jump sound

### Why
This keeps input mapping isolated from gameplay logic and avoids `KeyboardInputs` becoming another conditional-heavy controller.

---

## 5) Jump sound behavior fixed for key repeat
### What changed
- `PlayJumpSoundCommand` is dispatched from keyboard bindings.
- `GamingState` guards repeated jump sound with `jumpSoundArmed` and resets on jump release:
  - `src/main/java/main/controller/state/GamingState.java`

### Why
Without guard, holding jump triggers OS key repeat and repeatedly replays jump sound.

---

## 6) Observer pattern was removed; direct controller flow is used
### What changed
- Deleted observer interface:
  - `src/main/java/main/model/observerEvents/GameObserver.java` (removed)
- Removed observer lists/notify flow from model/controller.
- `Game` now handles model side effects directly:
  - `handleModelSideEffects()`
  - `recordLevelCompletion()`
  - file: `src/main/java/main/controller/Game.java`

### Why
The observer setup added indirection without meaningful decoupling in this codebase. Direct calls are clearer and simpler to maintain.

---

## 7) Audio dependency was removed from model
### What changed
- Model package no longer imports/uses `AudioController`.
- Audio calls are in controller/state classes only:
  - `src/main/java/main/controller/Game.java`
  - `src/main/java/main/controller/state/GamingState.java`
  - `src/main/java/main/controller/state/MenuState.java`
  - `src/main/java/main/controller/state/LeaderboardState.java`
  - `src/main/java/main/controller/state/LevelSelectState.java`

### Why
Audio playback is a side effect and belongs to controller/application flow, not model rules.

---

## 8) Leaderboard file parsing moved out of view
### What changed
- Added model-side leaderboard service/data model:
  - `src/main/java/main/model/leaderboard/LeaderboardService.java`
  - `src/main/java/main/model/leaderboard/ScoreEntry.java`
- `Leaderboard` view consumes data source, does not parse score files directly.

### Why
File I/O/parsing is not a view responsibility. View should render data, not load/parse it.

---

## 9) GamePanel/GameWindow are passive wiring points
### What changed
- `GamePanel` exposes `attachInputListeners(...)` and delegates rendering:
  - `src/main/java/main/view/GamePanel.java`
- `GameWindow` forwards focus-loss callback through panel:
  - `src/main/java/main/view/GameWindow.java`

### Why
Input composition and focus behavior should route through controller-owned flow, not through view internals.

---

## 10) GameConfig extraction removed model dependency on controller constants
### What changed
- Added shared constants:
  - `src/main/java/utilities/GameConfig.java`
- Replaced direct dependence on `Game.GAME_WIDTH/HEIGHT/SCALE` in model/utility code.

### Why
Model/utility code should not depend on controller class constants.

---

## Concrete Examples of Responsibility Moves

1. Player drawing:
- From model entity behavior
- To `src/main/java/main/view/render/PlayerRenderer.java`

2. Spike/trigger platform/trigger spike/spawn platform rendering:
- From model level/entity drawing methods
- To `src/main/java/main/view/render/LevelRenderer.java`

3. Menu/level select/leaderboard transitions:
- From view directly mutating game state
- To action callbacks handled by controller state classes

4. Transition side effects (audio + score persistence):
- From observer indirection
- To direct controller flow in `src/main/java/main/controller/Game.java`

5. Keyboard behavior dispatch:
- From branching logic in input handler
- To command bindings + command execution in `src/main/java/main/controller/inputs/KeyboardInputs.java`

---

## Current Design Outcome
- **Improved MVC adherence**:
  - Rendering moved into view renderers
  - Views are callback-based and more passive
  - Input is command-routed
  - Controller owns transitions/side effects
- **Reduced accidental complexity**:
  - Observer indirection removed
  - Input flow is explicit and centralized
- **Better modularity than baseline**:
  - Less cross-layer reach-through
  - Clearer ownership of responsibilities

---

## Remaining Technical Debt (Known)
These are still present and should be next cleanup targets:
- `Game` still holds many responsibilities (composition + loop + transition policy + some orchestration)
- Model still uses `java.awt.geom.Rectangle2D` for hitboxes (acceptable pragmatically, but not pure UI-independence)
- Some dead/redundant methods/fields remain (e.g., unused helpers and duplicate leaderboard action paths)

