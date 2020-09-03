package checkers.bot.villarroel_a;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.*;

public class VillarroelBot extends CheckersBoard implements CheckersPlayer {

    public VillarroelBot() {
        super();
    }

    @Override
    public CheckersMove play(CheckersBoard board) {

        Player current_player = board.getCurrentPlayer();

        int depth = 4;
        CheckersMove best_option = null;

        try {
            System.out.println("PIEZAS NEGRAS : " + board.countPiecesOfPlayer(current_player));
            System.out.println("PIEZAS ROJAS : " + board.countPiecesOfPlayer(otherPlayer(current_player)));
            System.out.println("PIEZAS REINAS NEGRAS : " + getNumberOfQueens(board, (current_player)));
            System.out.println("PIEZAS REINAS ROJAS : " + getNumberOfQueens(board, otherPlayer(current_player)));
            best_option = get_utility_Move(board, depth, current_player, true);

        } catch (BadMoveException e) {
            e.printStackTrace();
        }

        return best_option;
    }

    private CheckersMove get_utility_Move(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {
        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();
        Map<CheckersMove, Double> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state);
        CheckersMove best_move = null;

//        if (utility_list_map.isEmpty() && !possible_move_list.isEmpty()) {
//            return possible_move_list.get(ThreadLocalRandom.current().nextInt(possible_move_list.size()));
//        }
        if(possible_capture_list.isEmpty() && possible_move_list.isEmpty()){
            return null;
        }
        utility_list_map.forEach((key, value) -> System.out.println("Move=" + key + "  Utility=" + value));

        return Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

//        if (!possible_capture_list.isEmpty()) {
//            for (CheckersMove possible_capture : possible_capture_list) {
//                next_state = board.clone();
//                next_state.processMove(possible_capture);
//                if (next_state.equals(max_state)) {
//                    move = possible_capture;
//                }
//            }
//        } else {
//            if (!possible_move_list.isEmpty()) {
//                for (CheckersMove possible_move : possible_move_list) {
//                    next_state = board.clone();
//                    next_state.processMove(possible_move);
//                    if (next_state.equals(max_state)) {
//                        move = possible_move;
//                    }
//                }
//            }
//        }
//        return best_move;
//    }

    private double get_heuristic(CheckersBoard board, Player player) { // The heuristic depends on the number of pieces on the board.
        int plain_cost = 10;
        int queen_cost = 100;
        int number_of_my_plain_pieces = board.countPiecesOfPlayer(player) - getNumberOfQueens(board, player);
        int number_of_enemy_plain_pieces = board.countPiecesOfPlayer(otherPlayer(player)) - getNumberOfQueens(board, otherPlayer(player));

        return ((number_of_my_plain_pieces * plain_cost) + (getNumberOfQueens(board, player) * queen_cost)
                - (number_of_enemy_plain_pieces * plain_cost) - (getNumberOfQueens(board, otherPlayer(player)) * queen_cost)
        );
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

    private double get_utility_value(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {

        if (depth == 0) {
            return get_heuristic(board, player);
        }

        double valueOfminimax = 0;
        Map<CheckersMove, Double> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state);

        if (is_max_state) {
            valueOfminimax = Collections.<Double>max(utility_list_map.values());
        } else {
            valueOfminimax = Collections.<Double>min(utility_list_map.values());
        }
        return valueOfminimax;
    }

    public Map<CheckersMove, Double> get_move_and_utility_for_successors_list(CheckersBoard board, int depth, Player player, boolean max_state) throws BadMoveException {

        Map<CheckersBoard, CheckersMove> successors = generate_successors(board);
        Map<CheckersMove, Double> utility_and_move_list_map = new HashMap<>();

        if (successors != null) {
            for (Map.Entry<CheckersBoard, CheckersMove> entry : successors.entrySet()) {
                CheckersBoard successor_board = entry.getKey();
                CheckersMove successor_move = entry.getValue();
                max_state = !max_state;
                utility_and_move_list_map.put(successor_move, get_utility_value(successor_board, depth - 1, otherPlayer(player), max_state));
            }
        }
        return utility_and_move_list_map;
    }

    private Map<CheckersBoard, CheckersMove> generate_successors(CheckersBoard board) throws BadMoveException {

        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

        if (possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
            return null;
        }

        Map<CheckersBoard, CheckersMove> successors = new HashMap<>();

        if (!possible_capture_list.isEmpty()) {
            for (CheckersMove checkers_capture : possible_capture_list) {
                CheckersBoard new_state = board.clone();
                new_state.processMove(checkers_capture);
//                successors.add(new_state);
                successors.put(new_state, checkers_capture);
            }
        } else {
            if (board.isMovePossible()) {
                for (CheckersMove checkers_move : possible_move_list) {
                    CheckersBoard new_state = board.clone();
                    new_state.processMove(checkers_move);
//                successors.add(new_state);
                    successors.put(new_state, checkers_move);
                }
            }
        }
        return successors;
    }

}
