# DETAILS FROM "HUGE CHANGES"
## 1. PuzzleBaseFragment
### 1.1. Context
- In the past, both UI rendering and Data update were handled by a function called "animation", which is fucking stupid.
- There were no implementations to prevent animation() from repeating itself.
- Both puzzle completion and UI rendering are in the same file, which makes maintenance painful. With Progress and Audio introduced, maintenance becomes unbearable.
### 1.2. Changes
- animation():
  - animation(int index) is now updatePuzzle(ImageView puzzleBox, int boxIndex).
  - Old logic from animation() is now split into Data updating, UI rendering and Audio handling. updatePuzzle runs each of them like an event bus.
- Session parameters and functions are added to deal with unwanted repeating logic:
  - Parameters:
    - HashSet<Interger> completedThisRun (a set of solved box indexes),
    - boolean puzzleCompletedThisRun (true if all boxes in currently running puzzle are solved).
  - Functions:
    - protected HashSet<Interger> getCompletedThisRun(): returns completedThisRun,
    - protected boolean isPuzzleCompletedThisRun(): returns puzzleCompletedThisRun.
    - protected abstract int getTotalBoxes(): returns total number of boxes in a puzzle. 
- Migrated Data update logic to new classes (PuzzleProgress and PuzzleCompletion) for easier maintenance.
### 1.3. Mandatory implementations
- All puzzle fragments must implement getTotalBoxes().
- All puzzle fragments with more than 1 boxes (except Puzzle 7) must load its progress in onViewCreated(). Example:
```Java
@Override
public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    for (int index : getCompletedThisRun()) {
      applyCurrentProgress(boxes[index]);
    }
}
```

- All boxes should be cached (initialized as a global variable and saved during onCreateView). Example:
- - Multiple boxes (puzzle 1):
```Java
// BOXES ARRAY AND BOX IDS
private final ImageView[] boxes = new ImageView[6];
int[] boxIds = {
    R.id.imageView0,
    R.id.imageView1,
    R.id.imageView2,
    R.id.imageView3,
    R.id.imageView4,
    R.id.imageView5
};
```
```Java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_puzzle1, container, false);

    // CACHE
    for (int i = 0; i < boxes.length; i++) {
        boxes[i] = root.findViewById(boxIds[i]);
    }
    return root;
}
```
- - One box (puzzle 4):
```Java
private ImageView proximityBox;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_puzzle4, container, false);

    proximityBox = root.findViewById(R.id.imageView0);

    return root;
}
```
- Using puzzleUpdate(ImageView puzzleBox, int boxIndex):
  - Requires at least 1 input: puzzleBox. By default, boxIndex = 0.
  - Example (puzzle 1):
```Java
// Kiểm tra hoàn thành puzzle
if (gravityX < -THRESHOLD) {
  updatePuzzle(boxes[RIGHT], RIGHT);
}
if (gravityX > THRESHOLD) {
  updatePuzzle(boxes[LEFT], LEFT);
}
if (gravityY < -THRESHOLD) {
  updatePuzzle(boxes[TOP], TOP);
}
if (gravityY > THRESHOLD) {
  updatePuzzle(boxes[BOTTOM], BOTTOM);
}
if (gravityZ < -THRESHOLD) {
  updatePuzzle(boxes[MIDDLE_TOP], MIDDLE_TOP);
}
if (gravityZ > THRESHOLD) {
  updatePuzzle(boxes[MIDDLE_BOTTOM], MIDDLE_BOTTOM);
}
```
## 2. General changes to all puzzle fragments
- Minimal changes were made (using puzzleUpdate and loading progress) to accomodate the new gameplay loop without altering core logic.
- Moderately improved code maintainability and readability.
## 3. Specific logic changes
### 3.1. Puzzle Fragment 1
- Context:
  - Fluid logic was an absolute fucking mess, boxes positions make no sense (Puzzle1Fragment.java).
  - Diabolical fluid parameters (activity_puzzle1.xml).
- Changes:
  - Puzzle1Fragment.java:
    - Added custom view FluidView for flat fluid simulation (using only gravityX and gravityY).
    - Boxes positions became more intuitive.
  - Added bubbles that work with gravityZ.
### 3.2. Puzzle Fragment 29
- Context: Old logic changes UI over time by looping through a timer 50 times (the timer value was also re-assigned every loop).
- Changes:
  - Runs an animation that gradually change the background from start color to final color, cancel that animation upon letting go.
  - Once animation ends without being cancelled, updatePuzzle.
