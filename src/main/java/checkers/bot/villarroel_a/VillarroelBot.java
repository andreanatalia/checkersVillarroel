package checkers.bot.villarroel_a;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.*;

public class VillarroelBot extends CheckersBoard implements CheckersPlayer {

    Map<CheckersMove, Integer> utility_list_map;

    CheckersBoard initBoard;
    private char[][] checkers_board;

    public VillarroelBot() {
        super();
        this.checkers_board = getBoard();
        initBoard = CheckersBoard.initBoard();
    }


    @Override
    public CheckersMove play(CheckersBoard board) {

        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

        int maxUtility = Integer.MIN_VALUE;
        CheckersMove best_option = null;

        if (possible_capture_list.isEmpty()) {
            for (CheckersMove move_successor : possible_move_list) {
                int utility = get_utility(board);
                System.out.println("assdfa " + utility);

                if (maxUtility < utility) {
                    maxUtility = utility;
                    best_option = move_successor;
                }

            }

        } else {
            for (CheckersMove capture_successor : possible_capture_list) {
                int utility = get_utility(board);
                System.out.println("??????" + utility);

                if (maxUtility < utility) {
                    maxUtility = utility;
                    best_option = capture_successor;
                }
            }
        }

//        for (Map.Entry<CheckersMove, Integer> utility : utility_list_map.entrySet()) {
//                best_option = Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey();
//            }

 /*
        if (possible_capture_list.isEmpty()) {
            for (CheckersMove move_successor : possible_move_list) {

                for (Map.Entry<CheckersMove, Integer> checkersMoveIntegerEntry : utility_list_map.entrySet()) {
                int utility = get_utility(board);
                    if (checkersMoveIntegerEntry.getKey() == move_successor && checkersMoveIntegerEntry.getValue() == utility) {
                if (maxUtility < utility) {
                    maxUtility = utility;

                    best_option = move_successor;
                }
            }
                }
            }

        } else {
            for (CheckersMove capture_successor : possible_capture_list) {
                for (Map.Entry<CheckersMove, Integer> checkersMoveIntegerEntry : utility_list_map.entrySet()) {
                int utility = get_utility(board);
//
                    if (checkersMoveIntegerEntry.getKey() == capture_successor && checkersMoveIntegerEntry.getValue() == utility) {
//
                if (maxUtility < utility) {
                    maxUtility = utility;
                    best_option = capture_successor;
                }
                    }
                }
            }
        }
        */
        return best_option;

    }

    public boolean enemyCannotMoveOrCapture() {
        switchTurn();
        boolean enemyCannotMoveOrCapture = false;

        if (!isMovePossible() && !isCapturePossible()) {
            enemyCannotMoveOrCapture = true;
        }
        // switch the turn back to the real "current player"
        switchTurn();
        return enemyCannotMoveOrCapture;

    }

    public int final_state_utility() {

        if (!isCapturePossible() && !isMovePossible()) {
            if (enemyCannotMoveOrCapture()) {
                System.out.println("Cero");
                return 0; //Empate

            } else {
                System.out.println("MENOS Uno");
                return -1;
            }
        }
        System.out.println("MAS Uno");

        return 1;

    }

    public boolean enemy_can_capture() {
        switchTurn();
        boolean enemyCapture = false;

        if (isCapturePossible()) {
            enemyCapture = true;
        }

        switchTurn();
        return enemyCapture;
    }

    private List<CheckersBoard> generateSucessors(List<CheckersMove> possible_capture_list, List<CheckersMove> possible_move_list) {
        if (enemyCannotMoveOrCapture() && !isCapturePossible() && !isMovePossible() && !possible_capture_list.isEmpty() && !possible_move_list.isEmpty()) {
            return Collections.emptyList();
        }
        
     /*
        if (isCapturePossible()) {
            for (CheckersMove possible_capture : possible_capture_list) {
                System.out.println("Is capture is possible " + possible_capture.getStartRow() +" " + possible_capture.getStartCol());
                CheckersBoard child = clone();
                try {
                    child.processMove(possible_capture);
                } catch (BadMoveException e) {
                    e.printStackTrace();
                }
                successors.add(0, child);
            }
        } else {
            if (isMovePossible()) {
                for (CheckersMove possible_move : possible_move_list) {
                    System.out.println("Is move is possible " + isMovePossible());
                    CheckersBoard child = clone();
                    try {
                        child.processMove(possible_move);
                    } catch (BadMoveException e) {
                        e.printStackTrace();
                    }
                    successors.add(child);
                }
            }
        }*/

        List<CheckersBoard> successors = new ArrayList<>();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (isCapturePossible() || isMovePossible()) {
                    CheckersBoard child = this.clone();
                    try {
                        child.processMove(CheckersMove.builder().build());
                    } catch (BadMoveException e) {
                        e.printStackTrace();
                    }
                    successors.add(child);
                }
            }
        }

        return successors;
    }

    /* if( possible_capture_list.size() > 1 ) {
         for (CheckersMove possible_capture : possible_capture_list) {
             if ( enemy_can_capture() ) {
                utilities = possible_capture_list.stream().map(move_successor -> getUtility(board));
             } else {
                 if (isMovePossible()) {
     }*/
    public int get_utility(CheckersBoard board) {

        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();
        //        generateSucessors();
        generateSucessors(possible_capture_list, possible_move_list);
        if ((possible_capture_list.size() <= 1 && possible_move_list.size() <= 1)) {
//                || (countPiecesOfPlayer(Player.BLACK) == countPiecesOfPlayer(Player.RED)
//                && countPiecesOfPlayer(Player.BLACK) == 1)) {
//            return final_state_utility();
            if (!isCapturePossible() && !isMovePossible()) {
                if (enemyCannotMoveOrCapture()) {
                    System.out.println("Cero");
                    return 0; //Empate
                } else {
                    System.out.println("MENOS");
                    return -1;
                }
            }
            System.out.println("MAS");
            return 1;
        }
        utility_list_map = new HashMap<>();

            for (CheckersMove possible_move : possible_move_list) {
                utility_list_map.put(possible_move, get_utility(clone()));
                System.out.println("Posible Move: " + possible_move.getStartCol());

            }
            for (CheckersMove possible_capture : possible_capture_list) {
                utility_list_map.put(possible_capture, get_utility(clone()));
                System.out.println("Posible Capture: " + possible_capture.getStartCol());
            }


        if (otherPlayer() == Player.BLACK) {
            return Collections.min(utility_list_map.values());
        } else {
            return Collections.max(utility_list_map.values());
        }
    }
}
