import java.util.*;
public class FinalProject{
    static ArrayList<Coordinate> pegs = new ArrayList<Coordinate>();
    static ArrayList<Coordinate> scores = new ArrayList<Coordinate>();
    static Coordinate ball;
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        boolean condition = true;
        int probability = 0;
        int score = 0; 
        int totalPoints = 0;
        int points = 0;
        boolean input = false; 
        boolean scoreDupe = false;
        int numAttempts = 0;
        int speed = 0;
        int end = 0; //int to end if ball goes around the board enough times 
        int ender = points; //variable to end when all points are clicked
        boolean valid = false;
        Board board = new Board(21, 20);
        for(int r = 1; r < 20; r += 4){ //for loop for first set of black pegs on plinko board 
            for (int c = 1; c < 20; c += 2){
                board.putPeg("black", r, c);
                pegs.add(new Coordinate(r, c));
            }
        }
        for(int i = 3; i < 20; i += 4){ //second set, together these 2 loops make the plinko board
            for(int j = 0; j < 20; j += 2){
                board.putPeg("black", i, j);
                pegs.add(new Coordinate(i, j)); 
            }
        }
        while(!input){ //while loop to ask user again if the input is over 10 or under 1 or not an int 
             board.displayMessage("How many point pegs do you want? (1-10) (Please type in the terminal to answer your questions.)");
            try{
                points = scan.nextInt();
                if(points <= 10 && points >= 1){
                    input = true; //if within limit, set to true and go out of loop
                }
                else{ //if not, tell them it is over/under, then restart while loop
                    System.out.println("Input is over 10 or under 1, please try again");
                } 
            }
            catch(InputMismatchException e){//if not an int, catch it and 
                System.out.println("Input is not an integer, try again.");
                scan.nextLine();
            }
        }
        for(int i = 0; i < points;i++){ //for loop to make sure point pegs dont overlap 
            int prizeCol = randomPrize();
            scoreDupe = false;
            for(int j = 0; j < scores.size();j++){
                if(scores.get(j).getCol() == prizeCol){
                    scoreDupe = true;
                }
            } 
            if(scoreDupe == true){//if anything in scores is already in this prizeCol, go back one index and reassign that peg a new coordinate
                i--;
            }
            else{
                board.putPeg("yellow", 20, prizeCol); //prize peg place
                scores.add(new Coordinate (20, prizeCol));//prize peg coord
            }
            scoreDupe = false;
        }   
        input = false;
        while(!input){ 
            board.displayMessage("How many times do you want the ball to go around the board?");
            try{
                numAttempts = scan.nextInt();
                if(numAttempts > 0 && numAttempts < 2147483647){ // < all real ints 
                    input = true;
                } 
                else{   
                    System.out.println("Input is not a valid input, try again.");
                    input = false;
                }
            }
            catch(InputMismatchException e){//if not int, catch it and restart loop 
                System.out.println("Input is not an integer, try again.");
                input = false;
            }
            scan.nextLine(); //collect any string ints for no errors
        }
        input = false;
        while(!input){
            board.displayMessage("How fast do you want the ball to go? (in milliseconds per tick, the lower the faster)");
            try{
                speed = scan.nextInt();
                if(speed>0){
                    input = true;
                }
                else
                System.out.println("Input is 0 or less, please input 1 or higher.");
            }
            catch(InputMismatchException e){
                System.out.println("Input is not an integer, try again.");
                input = false;
            }
            scan.nextLine();
        }
        board.displayMessage("Please click anywhere on the top row.");
        Coordinate click = new Coordinate(0, 0);
        while(!valid){ //while loop just to make sure user clicks on first row.
            click = board.getClick();
            if(click.getRow() != 0){  
                board.displayMessage("restart (click on first row)");
            }
            else{
                valid = true;
            }
        }
        board.putPeg("cyan", click.getRow(), click.getCol());
        ball = click; //assign ball coordinate to where user clicked
        while(condition == true){
            try{//try catch to make delay with each tick 
                Thread.sleep(speed); //speed = milliseconds user inputted
            }catch(InterruptedException e){}
            board.removePeg(ball.getRow(), ball.getCol());
            ball = new Coordinate(ball.getRow()+1, ball.getCol()); //replace ball down one row
            board.putPeg("cyan", ball.getRow(), ball.getCol());
            for(int j = 0; j < pegs.size();j++){ //for loop that checks if ball overlaps with a black peg, and if it does send it left or right
                int leftOrRight = randomNum();
                if(ball.getRow() == pegs.get(j).getRow() && ball.getCol() == pegs.get(j).getCol()){                
                    board.putPeg("black", ball.getRow(), ball.getCol()); 
                    if(ball.getCol() == 0 && leftOrRight == -1){
                        leftOrRight = 1;
                    } //these 2 if statements make sure the ball doesnt fall off the board by acting as if there is a wall there
                    if(ball.getCol() == 19 && leftOrRight == 1){
                        leftOrRight = -1;
                    }                   
                    ball = new Coordinate(ball.getRow(), (ball.getCol() + leftOrRight)); //give new coord after left or right 
                    for(int i = 0; i < points; i++){
                        if(ball.getRow() == 19 && ball.getCol() == scores.get(i).getCol()){ //when the ball is at the last row, check if it touched a point peg, and if it did give user 10 points
                            board.removePeg(scores.get(i).getRow(), scores.get(i).getCol()); 
                            System.out.println(" + 10 points!");
                            scores.remove(i);
                            scores.add(new Coordinate (0, 0));
                            totalPoints = totalPoints + 10;
                            ender--;
                            if(ender == 0){ //if all the point pegs have been touched, ender will be 0, and the code will end 
                                condition = false;
                            }
                        }
                    }
                }
                if(ball.getRow() == 20){ //when ball at last column, return to top
                    ball = new Coordinate(ball.getRow() - 20, ball.getCol());
                    board.removePeg(ball.getRow()+20, ball.getCol());
                    end++; //increment to end code later if runs out of attempts
                }
                if(end == numAttempts){
                    board.putPeg("cyan", 20, ball.getCol());
                    condition = false;
                }
                else{
                    board.putPeg("cyan", ball.getRow(), ball.getCol());
                }
            }
        }
        System.out.println("Your total points are: " + totalPoints);
    }
    /** A method that generates an integer, a random number that is either 1 or 2.
    * @return -1 if the randomNum int is 0, and 1 if the randomNum int is 1. Determines which way peg goes.
    * @return 0, in the case none of the if statements are fulfilled. 
     */
    public static int randomNum(){
        Random randomGenerator = new Random();
        int randomNum = randomGenerator.nextInt(2);  
        if(randomNum == 0){
            return -1;
        }
        if(randomNum == 1){
            return 1;
        }
        return 0;
    }
    /** A method that generates an integer, a random number which spans from even numbers 2-22
    * @return randomNums, an integer which represents the column of the prize pegs 
     */
    public static int randomPrize(){
        Random randomGenerator2 = new Random();
        int randomNums = 0;
            randomNums = randomGenerator2.nextInt(1, 11);
            randomNums = randomNums * 2;
            randomNums--;
        return randomNums;
    }
}
