private boolean[][] mines = new boolean[1][1];
private int[][] nums = new int[1][1];
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
            int startingX = Integer.parseInt(IO.readln());
            IO.print("startingY: ");
            int startingY = Integer.parseInt(IO.readln());
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

        else {
            IO.println("Invalid Command");
        }

    }
}
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