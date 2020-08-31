package checkers.bot.villarroel_a;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.*;
import java.util.stream.Stream;

public class VillarroelBot extends CheckersBoard implements CheckersPlayer {

    Map<CheckersMove, Integer> utility_list_map;

    CheckersBoard initBoard;
    CheckersPlayer player;
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
//        List<CheckersBoard> successors = generateSucessors(possible_capture_list, possible_move_list);

//        for(CheckersBoard succesor: successors){
           if(possible_capture_list.isEmpty()) {
               for (CheckersMove move_successor : possible_move_list) {
                   int utility = get_utility(board);
                   if (maxUtility < utility) {
                       maxUtility = utility;
                       best_option = move_successor;
                   }

               }
           } else {
               for (CheckersMove capture_successor : possible_capture_list) {
                   int utility = get_utility(board);
                   if (maxUtility < utility) {
                       maxUtility = utility;
                       best_option = capture_successor;
                   }
               }
           }
//        }

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
        if (!isCapturePossible() && !isMovePossible() && !possible_capture_list.isEmpty() && !possible_move_list.isEmpty()) {
            return Collections.emptyList();
        }

        List<CheckersBoard> successors = new ArrayList<>();
//
//        if (!possible_capture_list.isEmpty()) {
//            for (CheckersMove possible_capture : possible_capture_list) {
//                CheckersBoard child = clone();
//                try {
//                    child.processMove(possible_capture);
//                } catch (BadMoveException e) {
//                    e.printStackTrace();
//                }
//                successors.add(0, child);
//            }
//       } else {
//            if (!possible_move_list.isEmpty()) {
//                for (CheckersMove possible_move : possible_move_list) {
//                    CheckersBoard child = clone();
//                    try {
//                        child.processMove(possible_move);
//                    } catch (BadMoveException e) {
//                        e.printStackTrace();
//                    }
//                    successors.add(child);
//                }
//            }
//        }
//
//
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (isCapturePossible() || isMovePossible()) {
                    CheckersBoard child = clone();
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

        List<CheckersBoard> successors = generateSucessors(possible_capture_list, possible_move_list);

        if ((possible_capture_list.size() <= 1 || possible_move_list.size() <= 1)) {
//            return final_state_utility();

            if (possible_capture_list.size() ==0 && possible_move_list.size() == 0) { //num Piezas menores a otherPlayer
                System.out.println("Menos Uno");
                return (-1);

            } else {

                if ((countPiecesOfPlayer(Player.BLACK) == countPiecesOfPlayer(Player.RED)
                        && countPiecesOfPlayer(Player.BLACK) == 1)) {
                    System.out.println("Cero");
                    return (0);
                } else {
                    System.out.println("Mas Uno");
                    return (1);
                }
            }
        }
        utility_list_map = new HashMap<>();

        if (possible_capture_list.isEmpty()) {
            for (CheckersMove possible_move : possible_move_list) {
                utility_list_map.put(possible_move, get_utility(clone()));
            }
        } else {
            for (CheckersMove possible_capture : possible_capture_list) {
                utility_list_map.put(possible_capture, get_utility(clone()));
            }
        }


//      Stream<Integer> utilities = successors.stream().map(this::get_utility);

        if (otherPlayer() == Player.BLACK) {
            return Collections.min(utility_list_map.values());
//            return utilities.min(Integer::compareTo).orElseThrow();
        } else {
            return Collections.max(utility_list_map.values());
//            return utilities.max(Integer::compareTo).orElseThrow();
        }
    }

}
