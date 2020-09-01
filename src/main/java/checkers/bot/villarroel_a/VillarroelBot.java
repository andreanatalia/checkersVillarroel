package checkers.bot.villarroel_a;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import javax.annotation.processing.SupportedSourceVersion;
import java.util.*;
import java.util.stream.Stream;

public class VillarroelBot extends CheckersBoard implements CheckersPlayer {

//    Map<CheckersMove, Integer> utility_list_map;

    private Player current_player;

    private List<CheckersMove> possible_capture_list;
    private List<CheckersMove> possible_move_list;

    private List<CheckersMove> enemy_possible_capture_list;
    private List<CheckersMove> enemy_possible_move_list;

    private int depth;

    public VillarroelBot() {
        super();
    }


    @Override
    public CheckersMove play(CheckersBoard board) {

        current_player = board.getCurrentPlayer();

        possible_capture_list = board.possibleCaptures();
        possible_move_list = board.possibleMoves();
        enemy_possible_move_list = board.possibleCaptures(otherPlayer(current_player));
        enemy_possible_move_list = board.possibleCaptures(otherPlayer(current_player));

//        possible_capture_list.forEach(capt->System.out.println("Possible Capture " + capt.toString()));
//        board.possibleCaptures(otherPlayer(current_player)).forEach(capt->System.out.println("-> Possible Capture Other Player " + capt.toString()));
//        possible_move_list.forEach(capt->System.out.println("Possible Move " + capt.toString() ));
//        board.possibleMoves(otherPlayer(current_player)).forEach(capt->System.out.println("-> Possible Move Other Player" + capt.toString() ));

        int maxUtility = Integer.MIN_VALUE;
        CheckersMove best_option = null;

//        List<CheckersBoard> successors = generateSucessors();
        if (possible_capture_list.isEmpty()) {
            for (CheckersMove move_successor : possible_move_list) {
                int utility = get_utility(board, current_player);
                if (maxUtility < utility) {
                    maxUtility = utility;
                    best_option = move_successor;
                }

            }
        } else {
            for (CheckersMove capture_successor : possible_capture_list) {
                int utility = get_utility(board, current_player);
                if (maxUtility < utility) {
                    maxUtility = utility;
                    best_option = capture_successor;
                }
            }
        }

//        for (Map.Entry<CheckersMove, Integer> utility : utility_list_map.entrySet()) {
//                best_option = Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey();
//            }


        return best_option;

    }

    public int get_utility(CheckersBoard board, Player player) {

//       System.out.println("PIEZAS NEGRAS : " + board.countPiecesOfPlayer(current_player));
//       System.out.println("PIEZAS ROJAS : " + board.countPiecesOfPlayer(otherPlayer(current_player)));

        if (board.isCapturePossible(current_player) || board.isMovePossible(current_player)) {
            return get_heuristic(board);
        }
        return 0;
//        return miniMax(board, player);
    }

    private int get_heuristic(CheckersBoard board) {

        int plain_cost = 10;
        int queen_cost = 100;

        if (!possible_move_list.isEmpty() && !possible_capture_list.isEmpty()) {
            return ((board.countPiecesOfPlayer(current_player) - getNumberOfQueens(board, current_player) * plain_cost)
                    + (getNumberOfQueens(board, current_player) * queen_cost)
                    - ((board.countPiecesOfPlayer(otherPlayer(current_player)) - getNumberOfQueens(board, otherPlayer(current_player)) * plain_cost))
                    - (getNumberOfQueens(board, otherPlayer(current_player)) * queen_cost)
            );
        } else {
            return ((board.countPiecesOfPlayer(otherPlayer(current_player)) - getNumberOfQueens(board, otherPlayer(current_player)) * plain_cost)
                    + (getNumberOfQueens(board, otherPlayer(current_player)) * queen_cost)
                    - ((board.countPiecesOfPlayer(current_player) - getNumberOfQueens(board, current_player) * plain_cost))
                    - (getNumberOfQueens(board, current_player) * queen_cost)
            );
        }
    }

    public int getNumberOfQueens(CheckersBoard board, Player player) {
        int numberOfqueens = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getBoard()[i][j] == 'R' && player == Player.RED || board.getBoard()[i][j] == 'B' && player == Player.BLACK) {
                    numberOfqueens++;
                }
            }
        }
        return numberOfqueens;
    }

    private int miniMax(CheckersBoard board, Player player) {
//        utility_list_map = new HashMap<>();

        Stream<Integer> utilities;

        if (!possible_capture_list.isEmpty()) {
//            utilities = possible_capture_list.stream().map(move -> move.get_utility(current_player));
            utilities = possible_capture_list.stream().map(move -> get_utility(board, current_player));

        } else {
            utilities = possible_move_list.stream().map(capture -> get_utility(board, current_player));
        }
//       for (CheckersMove possible_capture : possible_capture_list) {
//            utility_list_map.put(possible_capture, get_utility( current_player));
//        }

//        for (CheckersMove possible_move : possible_move_list) {
//            utility_list_map.put(possible_move, get_utility( current_player));
//        }

//      Stream<Integer> utilities = successors.stream().map(this::get_utility);
        if (player == current_player) { //|| isCapturePossible(current_player) && isCapturePossible(otherPlayer(current_player))) {
//            return Collections.max(utility_list_map.values());
            return utilities.max(Integer::compareTo).orElseThrow();
        }
//        return Collections.min(utility_list_map.values());
        return utilities.min(Integer::compareTo).orElseThrow();

//        return 0;
    }


    private List<CheckersBoard> generateSucessors(CheckersBoard board) {

        if (!board.isCapturePossible(current_player) && !board.isMovePossible(current_player)
                && possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
            return Collections.emptyList();
        }

        List<CheckersBoard> successors = new ArrayList<>();

        if (!possible_capture_list.isEmpty()) {
            for (CheckersMove possible_capture : possible_capture_list) {
                CheckersBoard child = clone();
                try {
                    child.processMove(possible_capture);
                    successors.add(0, child);
                } catch (BadMoveException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!possible_move_list.isEmpty()) {
                for (CheckersMove possible_move : possible_move_list) {
                    CheckersBoard child = clone();
                    try {
                        child.processMove(possible_move);
                        successors.add(child);
                    } catch (BadMoveException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        for (int y = 0; y < 8; y++) {
//            for (int x = 0; x < 8; x++) {
//                if (isCapturePossible(current_player) || isMovePossible(current_player)) {
//                    CheckersBoard child = clone();
//                    try {
//                        child.processMove(CheckersMove.builder().build());
//                    } catch (BadMoveException e) {
//                        e.printStackTrace();
//                    }
//                    successors.add(child);
//                }
//            }
//        }

        return successors;
    }

    public Integer max_state(CheckersBoard board) {
        int maxUtility = -Integer.MAX_VALUE;
        if (!board.isCapturePossible(current_player) && board.isMovePossible(current_player)) {
//            List<CheckersMove> possible_moves = possible_move_list;

        }

        return maxUtility;
    }
}
