import java.util.ArrayList;
import java.util.LinkedHashSet;

private boolean[][] mines = new boolean[1][1];
private int[][] nums = new int[1][1];
private int startingX = 0;
private int startingY = 0;
private int mineAmount;
void main() {
    String input="";
    while (!input.equals("quit")) {
        input = IO.readln();

        //help
        if(input.equals("help"))
            IO.println("place -> do this first to generate mines \n" +
                    "board -> print the full board\n" +
                    "solve -> attempt to solve current board\n" +
                    "quit -> exit program");

        //place mines
        else if(input.equals("place")){
            IO.print("width: ");
            int width = Integer.parseInt(IO.readln());
            IO.print("height: ");
            int height = Integer.parseInt(IO.readln());
            IO.print("mine amount: ");
            mineAmount = Integer.parseInt(IO.readln());
            IO.print("starting x: ");
            startingX = Integer.parseInt(IO.readln());
            IO.print("startingY: ");
            startingY = Integer.parseInt(IO.readln());
            mines = placeMines(width,height,mineAmount,startingX,startingY);
            nums = getNums(mines);
        }

        //print mines
        else if(input.equals("board")) {
            int x = 0;
            int y = 0;
            while(y < mines[0].length){
                x=0;
                while(x < mines.length){
                    if(mines[x][y]) IO.print("#");
                    else {
                        IO.print(nums[x][y]);
                    }
                    x++;
                }
                IO.print("|\n");
                y++;
            }
        }

        //attempt to solve current board
        else if(input.equals("solve")) {
            solve(mines, nums, startingX, startingY, mineAmount);
        }

        //quit. doesnt need to do anything, loop closes itself
        else if(input.equals("quit")) {

        }
        else {
            IO.println("Invalid Command");
        }

    }
}

//takes width, height, minesAmount, starting cords and makes a randomized mine layout
boolean[][] placeMines(int width, int height, int mineAmount, int startingX, int startingY) {
    boolean[][] runningMines = new boolean[width][height];
    Random rand = new Random();

    while (mineAmount > 0) {
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);
        //check if this spot is next to the starting location
        boolean nextToStart = ((x>=startingX-1&&x<=startingX+1) && (y>=startingY-1&&y<=startingY+1));
        if(!runningMines[x][y] && !nextToStart) {
            runningMines[x][y] = true;
            mineAmount--;
            IO.println("Mine placed at (" + x + ", " + y + ").");
        }
    }

    return runningMines;
}

//takes a mine layout and generates numbers
int[][] getNums(boolean[][] mines) {
    int[][] runningNums = new int[mines.length][mines[0].length];
    int x = 0;
    int y = 0;
    while(y < mines[0].length) {
        x=0;
        while(x < mines.length) {
            if(mines[x][y]) {
                x++;
                continue;
            }
            int num = 0;
            if(x > 0 && y > 0) num += ((mines[x-1][y-1]) ? 1 : 0);
            if(y > 0) num += ((mines[x][y-1]) ? 1 : 0);
            if(y>0 && x < mines.length-1) num += ((mines[x+1][y-1]) ? 1 : 0);
            if(x < mines.length-1) num += ((mines[x+1][y]) ? 1 : 0);
            if(x > 0) num += ((mines[x-1][y]) ? 1 : 0);
            if(y< mines[x].length-1 && x < mines.length-1) num += ((mines[x+1][y+1]) ? 1 : 0);
            if(y< mines[x].length-1 ) num += ((mines[x][y+1]) ? 1 : 0);
            if(y< mines[x].length-1 && x >0) num += ((mines[x-1][y+1]) ? 1 : 0);
            runningNums[x][y]=num;
            x++;
        }
        y++;
    }
        return runningNums;
}
void solve(boolean[][] mines, int[][] nums, int startingX, int startingY, int mineAmount) {
    boolean[][] uncovered = new boolean[mines.length][mines[0].length];
    int tilesUncovered = 0;
    boolean[][] flags = new boolean[mines.length][mines[0].length];
    int flagsPlaced = 0;
    boolean changed = true;

    //uncover starting position
    uncovered[startingX][startingY] = true;

    while(changed) {
        //if the surrounding spots of a cell are all mines or all empty, mark them as such
        while (changed) {
            changed = false;
            for (int x = 0; x < uncovered.length; x++)
                for (int y = 0; y < uncovered[0].length; y++) {
                    if (uncovered[x][y]) {
                        pos[] surroundings = surrounding(x, y, uncovered.length, uncovered[0].length);

                        //find adjusted num (num - flags)
                        int adjustedNum = nums[x][y];
                        for (pos p : surroundings) {
                            if (flags[p.x][p.y]) adjustedNum--;
                        }

                        //get the untouched surrounding tiles
                        ArrayList<pos> realSurroundings = new ArrayList<>();
                        for (pos p : surroundings) {
                            if (!uncovered[p.x][p.y] && !flags[p.x][p.y]) realSurroundings.add(p);
                        }

                        if (adjustedNum == 0) {
                            for (pos p : realSurroundings) {
                                uncovered[p.x][p.y] = true;
                                changed = true;
                                tilesUncovered++;
                            }
                        }

                        if (adjustedNum == realSurroundings.size()) {
                            for (pos p : realSurroundings) {
                                flags[p.x][p.y] = true;
                                changed = true;
                                flagsPlaced++;
                            }
                        }
                    }

                }
            if (changed) {
                IO.println("Something changed");
            } else {
                IO.println("Nothing changed");
            }
            printSolverBoard(nums, uncovered, mines, flags);
        }

        //brute force
        {
            changed = false;
            pos[] frontier = findFrontier(uncovered, flags);
            int n = frontier.length;
            int[] mineTally = new int[frontier.length];
            int validLayouts = 0;
            boolean[] isMine = new boolean[n];
            //find the tiles adjacent to frontier and uncovered
            Set<pos> activeTiles = new LinkedHashSet<>();
            for (pos p : frontier) {
                for (int x = p.x - 1; x <= p.x + 1; x++)
                    for (int y = p.y - 1; y <= p.y + 1; y++) {
                        if (x >= 0 && x < uncovered.length && y >= 0 && y < uncovered[0].length)
                            if (uncovered[x][y]) activeTiles.add(new pos(x, y));
                    }
            }

            //find each possible mine layout of the frontier mines
            for (long layout = 0; layout < (1L << n); layout++) {
                int minesInLayout = 0;
                for (int i = 0; i < n; i++) {
                    isMine[i] = ((layout >> i) & 1) == 1;
                    if (isMine[i]) minesInLayout++;
                }

                //make sure it doesnt go over the minecount
                if (minesInLayout > mineAmount - flagsPlaced) continue;

                //newFlags = flags, copy row by row
                boolean[][] newFlags = new boolean[flags.length][];
                for (int x = 0; x < flags.length; x++) {
                    newFlags[x] = flags[x].clone();
                }

                //place potential mines onto newFlags
                for (int i = 0; i < isMine.length; i++) {
                    if (isMine[i]) {
                        newFlags[frontier[i].x][frontier[i].y] = true;
                    }
                }

                //create activeTileAdjacentFlags to check against nums
                int[] activeTileAdjacentFlags = new int[activeTiles.size()];
                int index = 0;
                for (pos p : activeTiles) {
                    for (int x = p.x - 1; x <= p.x + 1; x++)
                        for (int y = p.y - 1; y <= p.y + 1; y++) {
                            if (x >= 0 && y >= 0 && x < uncovered.length && y < uncovered[0].length)
                                if (newFlags[x][y]) {
                                    activeTileAdjacentFlags[index]++;
                                }
                        }
                    index++;
                }

                //check activeTileAdjacentFlags against nums, discrepancy means its not a valid layout
                index = 0;
                boolean skip = false;
                for (pos p : activeTiles) {
                    if (activeTileAdjacentFlags[index] != nums[p.x][p.y]) skip = true;
                    index++;
                }
                if (skip) continue;

                //layout is valid
                {
                    validLayouts++;
                    for (int i = 0; i < frontier.length; i++) {
                        if (isMine[i]) mineTally[i]++;
                    }
                }
            }

            //uncover tiles that are always valid and tiles that are always mines
            for (int i = 0; i < frontier.length; i++) {
                if (mineTally[i] == 0) {
                    tilesUncovered++;
                    uncovered[frontier[i].x][frontier[i].y] = true;
                    changed = true;
                } else if (mineTally[i] == validLayouts) {
                    flagsPlaced++;
                    flags[frontier[i].x][frontier[i].y] = true;
                    changed = true;
                }
            }

            if (changed) {
                IO.println("Something changed");
            } else {
                IO.println("Nothing changed");
            }
            printSolverBoard(nums, uncovered, mines, flags);
        }
    }

    //if all flags placed OR all tiles uncovered, board is clear
    if (flagsPlaced == mineAmount
            || tilesUncovered == uncovered.length * uncovered[0].length - mineAmount) {
        IO.println("Board is solvable");
    } else {
        IO.println("Board is not solvable");
    }

}

//position record to be used in the solver
record pos(int x, int y) {}

//get the surround coords
pos[] surrounding(int x, int y, int width, int height) {
    int amount = 0;
    if (x > 0 && y > 0) amount++;
    if (x > 0) amount++;
    if (x > 0 && y < height - 1) amount++;

    if (y > 0) amount++;
    if (y < height - 1) amount++;

    if (x < width - 1 && y > 0) amount++;
    if (x < width - 1) amount++;
    if (x < width - 1 && y < height - 1) amount++;

    pos[] surroundings = new pos[amount];
    int at = 0;

    if (x > 0 && y > 0) {surroundings[at] = new pos(x - 1, y - 1); at++;}
    if (x > 0) {surroundings[at] = new pos(x - 1, y); at++;}
    if (x > 0 && y < height - 1) {surroundings[at] = new pos(x - 1, y + 1); at++;}

    if (y > 0) {surroundings[at] = new pos(x, y - 1); at++;}
    if (y < height - 1) {surroundings[at] = new pos(x, y + 1); at++;}

    if (x < width - 1 && y > 0) {surroundings[at] = new pos(x + 1, y - 1); at++;}
    if (x < width - 1) {surroundings[at] = new pos(x + 1, y); at++;}
    if (x < width - 1 && y < height - 1) {surroundings[at] = new pos(x + 1, y + 1); at++;}

    return surroundings;
}

//find the tiles that are adjacent to uncovered tiles but not uncovered (for solver)
pos[] findFrontier(boolean[][] uncovered, boolean[][] flags) {
    Set<pos> frontier = new LinkedHashSet<>();
    for (int x = 0; x < uncovered.length; x++)
        for (int y = 0; y < uncovered[0].length; y++) {
            if (uncovered[x][y])
                for (int xx = x - 1; xx <= x + 1; xx++)
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (xx < uncovered.length && xx >= 0)
                            if (yy < uncovered[0].length && yy >= 0)
                                if (!uncovered[xx][yy] && !(xx == x && yy == y) && !flags[xx][yy])
                                      frontier.add(new pos(xx, yy));
                    }

        }
    return frontier.toArray(new pos[0]);
}

//print board for solver
void printSolverBoard(int[][] nums, boolean[][] uncovered, boolean[][] mines, boolean[][] flags) {
    int y = 0;
    while (y < mines[0].length) {
        int x = 0;
        while (x < mines.length) {
            if (uncovered[x][y]) {
                if (flags[x][y]) {
                    IO.print(" UNCOVERED AND FLAGGED ");
                } else {
                    IO.print(nums[x][y]);
                }
            } else {
                if (flags[x][y]) {
                    IO.print("F");
                } else {
                    IO.print("#");
                }
            }
            x++;
        }
        IO.println();
        y++;
    }
}