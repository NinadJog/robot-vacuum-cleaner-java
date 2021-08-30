/**
 *   A robot vacuum cleaner that avoids obstacles and can only
 *   turn right.
 *
 *   Given a matrix of tiles, return the number of unique tiles
 *   cleaned by the vacuum cleaner, assuming it starts at the top
 *   left corner. Obstacles are denoted by 'X' and available tiles
 *   are denoted by '.'
 *
 *   Author:   Ninad Jog
 *   Updated:  August 30, 2021
 */

import java.util.*;

public class Vacuum {

    //---------------------------------------------------------------
    // NONE is when the robot vacuum cleaner cannot move in any direction
    public enum Heading { RIGHT, DOWN, LEFT, UP, NONE }

    public static void main (String []args){
        System.out.println ("Hello Robot Vacuum Cleaner");
        Vacuum robot = new Vacuum();
        robot.runTests();
    }

    //---------------------------------------------------------------
    // TEST FUNCTIONS OF THE SOLUTION

    public void runTests() {

        String[] R0 = getTestMatrix(0); // 2 x 2 matrix where movement from first tile is not possible
        String[] R1 = getTestMatrix(1);
        String[] R2 = getTestMatrix(2);
        String[] R3 = getTestMatrix(3);
        String[] R4 = getTestMatrix(4);   // Matrix with a single empty cell
        String[] R5 = getTestMatrix(5); // Matrix with two adjacent empty cells

        // Test solutions
        testSolution ("Matrix R1", R1); // 6
        testSolution ("Matrix R2", R2); // 15
        testSolution ("Matrix R3", R3); // 9
        testSolution ("Matrix R4", R4); // 1
        testSolution ("Matrix R5", R5); // 2

        /****** Test the helper functions
        testAdjacentCells (0, 0, R1);   // true, true, false, false, false (top left corner)
        testAdjacentCells (3, 1, R1);   // false, true, true, false, false
        testAdjacentCells (5, 2, R1);   // false, false, true, false, false (bottom right corner)
        testAdjacentCells (4, 0, R1);   // true, false, false, false, false (bottom right corner)

        testAdjacentCells (0, 0, R0);   // top left corner; all false
        testAdjacentCells (1, 1, R0);   // bottom right corner; all false

        testAdjacentCells (0, 0, R4);       // top left corner; all false

        //---------------------
        // Test next headings
        testNextHeading (0, 0, Heading.RIGHT, R1);  // RIGHT
        testNextHeading (2, 0, Heading.RIGHT, R1);  // DOWN
        testNextHeading (2, 0, Heading.DOWN, R1);  // DOWN
        testNextHeading (2, 1, Heading.DOWN, R1);   // LEFT
        testNextHeading (0, 1, Heading.LEFT, R1);   // UP

        // In R4
        System.out.println ("\nIn 1 x 1 matrix R4");
        testNextHeading (0, 0, Heading.RIGHT, R4);  // NONE
        testNextHeading (0, 0, Heading.DOWN, R4);   // NONE
        testNextHeading (0, 0, Heading.LEFT, R4);   // NONE
        testNextHeading (0, 0, Heading.UP, R4);     // NONE
        ******/
    } // runTests

    //---------------------------------------------------------------
    public String[] getTestMatrix (int testCase) {
        switch (testCase) {
            case 1: return new String[] { "...X..", "....XX", "..X..." };
            case 2: return new String[] {"....X..", "X......", ".....X.", "......." };
            case 3: return new String[] { "...X.", ".X..X", "X...X", "..X.." };
            case 4: return new String[] { "." };    // a single empty cell
            case 5: return new String[] {".."}; // Two adjacent empty cells
            case 0:
            default: return new String[] { ".X", "X." };
        }
    } // getTestMatrix

    // ---------------------------------------------------------------
    private void testSolution (String message, String[] R) {
        int answer = solution (R);
        System.out.println (message + " -> " + answer);
    }
    //---------------------------------------------------------------
    // THE SOLUTION

    // Given a grid of tiles, return the number of unique tiles cleaned
    // The println statements are only for debugging
    public int solution (String[] R) {

        Map <Cell, Heading> traveled    = new HashMap<>();
        Cell                curCell     = new Cell (0, 0);
        Heading             curHeading  = Heading.RIGHT; // Try to move right initially

        int iter = 0; // Used only for debugging

        while (true) {

            iter++;
            /***
             if (iter >= 10) {
             System.out.println ("Number of iterations exceeded 9, so quitting!");
             return 0;
             }

             System.out.println();
             System.out.println ("Iter = " + iter + " " + curCell + ", curHeading = " + curHeading);
             ****/
            /* The robot might intend to head in a certain direction such as right
             * or down, but there might be an obstacle in the way or it might have
             * reached a boundary, so it might need to turn right one or more times.
             * Determine the direction in which it should go.
             */
            Heading nextHeading = calculateNextHeading (curCell, curHeading, R);
            // System.out.println ("Next heading = " + nextHeading);

            /* The next direction is NONE only when the matrix has just a single cell
             */
            if (nextHeading == Heading.NONE) {
                // System.out.println ("  A1");
                return 1;
            }

            /* If the current cell has been cleaned before AND its heading is
             * the same as the current heading, the loop is completed, as it's
             * the second time that the robot has reached the same cell, intending
             * to head in the same direction as before. Which means we are done,
             * so return the count of the keys in the traveled map, as that's the
             * same as the number of cells cleaned.
             */
            else if (traveled.containsKey(curCell) && traveled.get(curCell) == nextHeading) {
                // System.out.println ("  A2");
                System.out.println ("\niter = " + iter);
                showTraveledCells (traveled);
                return traveled.size();
            }

            /* This cell has not been cleaned or has been cleaned but was approached
             * with a different destination heading in mind last time.
             */
            else {
                // System.out.println ("  A3");
                // If the cell was already cleaned, replace the existing heading with the new one
                traveled.put (curCell, nextHeading);
                curCell = calculateNextCell (curCell, nextHeading);
                curHeading = nextHeading;
            }
        }
    } // solution

    //---------------------------------------------------------------
    // Keep turning right until an available cell is encountered.
    // Return the direction the cell adjacent cell is in relative to
    // the current cell
    private Heading calculateNextHeading (Cell cell, Heading heading, String[] R)
    {
        Heading curHeading = heading;
        int     turns      = 0;

        while (true) {

            // If the robot is hemmed in from all sides, return NONE.
            if (curHeading == Heading.NONE || turns >= 4)
                return Heading.NONE;

            /* If there's no obstacle or matrix boundary in the adjacent cell,
             * the robot can continue with the current heading.
             */
            else if (isAdjacentCellAvailable (cell, curHeading, R))
                return curHeading;

            // Otherwise turn right
            else {
                curHeading = turnRight (curHeading);
                turns++;
            }
        }
    } // calculateNextHeading

    //---------------------------------------------------------------
    /* Returns true if there's no obstacle or boundary in the adjacent
     * cell in the direction of 'heading'; false otherwise.
     */
    public boolean isAdjacentCellAvailable (Cell cell, Heading heading, String[] R) {

        int     rows        = R.length;
        int     cols        = R[0].length(); // e.g. length of "....X.." == 7
        String  matrixRow   = R [cell.y];

        switch (heading) {
            case RIGHT:
                // Return false if cell is at right boundary OR
                // if the cell to the right of it contains an obstacle 'X'
                return !((cell.x == cols - 1) || (matrixRow.charAt (cell.x + 1) == 'X'));

            case LEFT:
                // Return false if cell is at LEFT boundary OR if cell to LEFT of it contains an obstacle
                return !((cell.x == 0) || (matrixRow.charAt (cell.x - 1) == 'X'));

            case DOWN:
                // Return false if cell is at BOTTOM boundary OR if cell BELOW it contains an obstacle
                return !((cell.y == rows - 1) || R [cell.y + 1].charAt (cell.x) == 'X');

            case UP:
                // Return false if cell is at TOP boundary OR if cell ABOVE it contains an obstacle
                return !((cell.y == 0) || R [cell.y - 1].charAt (cell.x) == 'X');

            default: return false;
        }

    } // isCellAvailable

    //---------------------------------------------------------------
    private Heading turnRight (Heading heading) {

        Heading newHeading = heading;
        switch (heading) {
            case RIGHT: newHeading = Heading.DOWN;  break;
            case DOWN:  newHeading = Heading.LEFT;  break;
            case LEFT:  newHeading = Heading.UP;    break;
            case UP:    newHeading = Heading.RIGHT; break;
            case NONE:  newHeading = Heading.NONE;  break;
        }
        return newHeading;
    }

    //---------------------------------------------------------------
    /* Returns the address of the next cell based upon the current
     * cell and the next heading. Assumes that the next heading is
     * correct. For example, if the current cell is in the last row
     * of the matrix, the heading should not be DOWN, because such
     * a cell does not exist. It's the caller's responsibility to
     * ensure that the heading is correct.
     */
    private Cell calculateNextCell (Cell cell, Heading heading)
    {
        int x = cell.x;
        int y = cell.y;

        switch (heading) {
            case RIGHT: x += 1; break;
            case DOWN:  y += 1; break;
            case LEFT:  x -= 1; break;
            case UP:    y -= 1; break;
            case NONE:          break;
        }
        return new Cell (x, y);

    } // calculateNextCell

    //---------------------------------------------------------------
    // FUNCTIONS TO TEST THE HELPER FUNCTIONS

    private void testNextHeading (int x, int y, Heading heading, String[] R) {
        Cell    cell        = new Cell (x, y);
        Heading newHeading  = calculateNextHeading (cell, heading, R);
        System.out.println (cell + " " + heading + " -> " + newHeading);
    }

    //---------------------------------------------------------------
    /* Test presence and occupancy of all 4 adjacent cells, starting from
     * RIGHT, followed by DOWN, LEFT, UP, and NONE.
     *
     * x is the column index while y is the row index
     */
    private void testAdjacentCells (int x, int y, String[] R) {

        Cell cell = new Cell (x, y);

        for (Heading heading: Heading.values()) {
            testAdjacentCell (cell, heading, R);
        }
        System.out.println();

    } // testAdjacentCells

    //---------------------------------------------------------------
    private void testAdjacentCell (Cell cell, Heading heading, String[] R) {

        boolean status = isAdjacentCellAvailable(cell, heading, R);
        System.out.println (cell + " " + heading.name() + ", " + status);

    } // testAdjacentCell

    //---------------------------------------------------------------
    private void showTraveledCells (Map <Cell, Heading> traveled)
    {
        for (Map.Entry <Cell, Heading> entry: traveled.entrySet()) {
            System.out.println ("    " + entry.getKey() + ": " + entry.getValue());
        }
    } // showTraveledCells

}
