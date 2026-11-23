import java.util.ArrayList;

private boolean[][] mines = new boolean[1][1];
private int[][] nums = new int[1][1];
private int startingX = 0;
private int startingY = 0;
void main() {
    String input="";
    while (!input.equals("quit")) {
        input = IO.readln();

        //help
        if(input.equals("help"))
            IO.println("place -> do this first to generate mines \n" +
                    "board -> print the full board\n" +
                    "quit -> exit program");

        //place mines
        else if(input.equals("place")){
            IO.print("width: ");
            int width = Integer.parseInt(IO.readln());
            IO.print("height: ");
            int height = Integer.parseInt(IO.readln());
            IO.print("mine amount: ");
            int amount = Integer.parseInt(IO.readln());
            IO.print("starting x: ");
            startingX = Integer.parseInt(IO.readln());
            IO.print("startingY: ");
            startingY = Integer.parseInt(IO.readln());
            mines = placeMines(width,height,amount,startingX,startingY);
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
void solve(boolean[][] mines, int[][] nums, int startingX, int startingY) {
    boolean[][] uncovered = new boolean[mines.length][mines[0].length];
    boolean[][] flags = new boolean[mines.length][mines[0].length];
    boolean changed = true;

    //uncover starting position
    uncovered[startingX][startingY] = true;

    //if the surrounding spots of a cell are all mines or all empty, mark them as such
    while(changed) {
        changed = false;
        for (int x = 0; x < uncovered.length; x++)
            for (int y = 0; y < uncovered[0].length; y++) {
                if (uncovered[x][y]) {
                    pos[] surroundings = surrounding(x,y, uncovered.length, uncovered[0].length);

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
                        }
                    }

                    if (adjustedNum == realSurroundings.size()) {
                        for (pos p : realSurroundings) {
                            flags[p.x][p.y] = true;
                            changed = true;
                        }
                    }
                }

        }
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