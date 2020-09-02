package checkers.bot.villarroel_a;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import javax.annotation.processing.SupportedSourceVersion;
import java.util.*;
import java.util.stream.Stream;

public class VillarroelBot extends CheckersBoard implements CheckersPlayer {

    private Player current_player;
    boolean is_a_max_state = true;
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
//        if (possible_capture_list.isEmpty()) {
//            for (CheckersMove move_successor : possible_move_list) {
//                int utility = get_utility(board, current_player);
//                if (maxUtility < utility) {
//                    maxUtility = utility;
//                    best_option = move_successor;
//                }
//
//            }
//        } else {
//            for (CheckersMove capture_successor : possible_capture_list) {
//                int utility = get_utility(board, current_player);
//                if (maxUtility < utility) {
//                    maxUtility = utility;
//                    best_option = capture_successor;
//                }
//            }
//        }

//        for (Map.Entry<CheckersMove, Integer> utility : utility_list_map.entrySet()) {
//                best_option = Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey();
//            }


        return best_option;

    }

    public double get_utility(CheckersBoard board, Player player) {

//       System.out.println("PIEZAS NEGRAS : " + board.countPiecesOfPlayer(current_player));
//       System.out.println("PIEZAS ROJAS : " + board.countPiecesOfPlayer(otherPlayer(current_player)));

        if (board.isCapturePossible(current_player) || board.isMovePossible(current_player)) {
            double heuristic = get_heuristic(board);
            System.out.println("POSIBLE CAPTURE : " + board.isCapturePossible(current_player) + " Heuristica " + heuristic);
            return heuristic;
//            return get_heuristic(board);
        }
        return 0;
//        return miniMax(board, player);
    }

    private double get_heuristic(CheckersBoard board) { // The heuristic depends on the number of pieces on the board.
        int plain_cost = 10;
        int queen_cost = 100;
//        System.out.println(" Number of Queens  BLACK= " + getNumberOfQueens(board, current_player));
//        System.out.println(" Number of Queens RED = " + getNumberOfQueens(board, otherPlayer(current_player)));

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
        // returns 0 if it is a "tie"
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


    private double minimax_value(CheckersBoard board, int depth) throws BadMoveException {

        if (depth == 0) {
            return get_utility(board, current_player);
        }

        if (is_a_max_state) {
            is_a_max_state = false;
            return Collections.max(get_utility_for_successors_list(board, current_player).values());
        } else {
            return Collections.min(get_utility_for_successors_list(board,current_player).values());
        }
    }

    public Map<CheckersBoard, Double> get_utility_for_successors_list(CheckersBoard board, Player player) throws BadMoveException {
        if (!board.isCapturePossible(current_player) && board.isMovePossible(current_player)) {
            List<CheckersMove> possible_moves = possible_move_list;
        }
        List<CheckersBoard> successors = generate_successors(board);
        Map<CheckersBoard, Double> utility_list_map = new HashMap<>();

        successors.forEach(successor -> {
            utility_list_map.put(successor, get_utility(successor, otherPlayer(player)));
        });

        return utility_list_map;
    }

    private List<CheckersBoard> generate_successors(CheckersBoard board) throws BadMoveException {
        CheckersBoard new_state = null;

        List<CheckersBoard> successors = new ArrayList<>();

        if (!possible_capture_list.isEmpty()) {
            for (CheckersMove checkers_capture : possible_capture_list) {
                new_state = board.clone();
                new_state.processMove(checkers_capture);
                successors.add(new_state);
            }
        } else {
            if (!possible_move_list.isEmpty()) {
                for (CheckersMove checkers_move : possible_move_list) {
                    new_state = board.clone();
                    new_state.processMove(checkers_move);
                    successors.add(new_state);
                }
            }
        }
        return successors;
    }
}
