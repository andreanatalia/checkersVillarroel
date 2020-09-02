package checkers.bot.villarroel_a;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import javax.swing.plaf.nimbus.State;
import java.util.*;
import java.util.stream.Stream;

public class VillarroelBot extends CheckersBoard implements CheckersPlayer {

//    boolean is_a_max_state = true;
//    private List<CheckersMove> possible_capture_list;
//    private List<CheckersMove> possible_move_list;

    public VillarroelBot() {
        super();
    }


    @Override
    public CheckersMove play(CheckersBoard board) {

        Player current_player = board.getCurrentPlayer();

        int depth = 7;
        CheckersMove best_option = null;
//        List<CheckersBoard> successors;
        try {
            best_option = get_utility_Move(board, depth, current_player, true);

        } catch (BadMoveException e) {
            e.printStackTrace();
        }

        /////////
//        try {
//            best_option = minimaxMove(board, depth, current_player, true);
//        } catch (BadMoveException e) {
//            e.printStackTrace();
//        }

        return best_option;

    }

    private CheckersMove get_utility_Move(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {
        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

        if (!possible_capture_list.isEmpty() && !possible_move_list.isEmpty()) {
            return null;
        }
//        double utility_value = get_utility_value(board, depth, player, is_max_state);
        Map<CheckersMove, Double> utility_list_map = get_utility_for_successors_list(board, depth, player, is_max_state);
        CheckersMove best_move = Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey();

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
        return best_move;
    }

    private double get_heuristic(CheckersBoard board, Player player) { // The heuristic depends on the number of pieces on the board.
        int plain_cost = 10;
        int queen_cost = 100;
//       System.out.println("PIEZAS NEGRAS : " + board.countPiecesOfPlayer(current_player));
//       System.out.println("PIEZAS ROJAS : " + board.countPiecesOfPlayer(otherPlayer(current_player)));
        return (((board.countPiecesOfPlayer(player) - getNumberOfQueens(board, player) * plain_cost)
                + (getNumberOfQueens(board, player) * queen_cost))
                - (((board.countPiecesOfPlayer(otherPlayer(player)) - getNumberOfQueens(board, otherPlayer(player)) * plain_cost))
                + (getNumberOfQueens(board, otherPlayer(player)) * queen_cost))
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

//        double valueOfminimax = 0;
        Map<CheckersMove, Double> utility_list_map = get_utility_for_successors_list(board, depth, player, is_max_state);

        if (is_max_state) {
            return Collections.max(utility_list_map.values());
        } else {
            return Collections.min(utility_list_map.values());
        }

    }

    public Map<CheckersMove, Double> get_utility_for_successors_list(CheckersBoard board, int depth, Player current_player, boolean max_state) throws BadMoveException {

        Map<CheckersBoard, CheckersMove> successors = generate_successors(board);
        Map<CheckersMove, Double> utility_and_move_list_map = new HashMap<>();

        if (successors != null) {
            for (Map.Entry<CheckersBoard, CheckersMove> entry : successors.entrySet()) {
                CheckersBoard successor_board = entry.getKey();
                CheckersMove successor_move = entry.getValue();
                max_state = !max_state;
                utility_and_move_list_map.put(successor_move, get_utility_value(successor_board, depth - 1, otherPlayer(current_player), max_state));

            }
        }
        return utility_and_move_list_map;
    }

    private Map<CheckersBoard, CheckersMove> generate_successors(CheckersBoard board) throws BadMoveException {

        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

        if (possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
//            return Collections.emptyList();
            return null;
        }

        Map<CheckersBoard,CheckersMove> successors = new HashMap<>();

        if (!possible_capture_list.isEmpty()) {
            for (CheckersMove checkers_capture : possible_capture_list) {
                CheckersBoard new_state = board.clone();
                new_state.processMove(checkers_capture);
//                successors.add(new_state);
                successors.put(new_state, checkers_capture);
            }
        } else {
            for (CheckersMove checkers_move : possible_move_list) {
                CheckersBoard new_state = board.clone();
                new_state.processMove(checkers_move);
//                successors.add(new_state);
                successors.put(new_state, checkers_move);
            }
        }
        return successors;
    }

    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    private double get_minimax_value(CheckersBoard board, int depth, Player player, boolean max_state) throws BadMoveException {

        if (depth == 0) {
            return get_heuristic(board, player);
        }

        double valueOfminimax = 0;
        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

        CheckersBoard next_step = null;
        if (max_state) {
            valueOfminimax = Double.MIN_VALUE;
            if (isCapturePossible(player)) {
                for (CheckersMove checkersMove : possible_capture_list) {
                    next_step = board.clone();
                    next_step.processMove(checkersMove);
                    max_state = !max_state;
                    double result = get_minimax_value(next_step, depth - 1, otherPlayer(player), max_state);
                    valueOfminimax = Math.max(result, valueOfminimax);
                }
            } else {
                for (CheckersMove checkersMove : possible_move_list) {
                    next_step = board.clone();
                    next_step.processMove(checkersMove);
                    max_state = !max_state;
                    double result = get_minimax_value(next_step, depth - 1, otherPlayer(player), max_state);
                    valueOfminimax = Math.max(result, valueOfminimax);
                }
            }
        } else {
            valueOfminimax = Double.MAX_VALUE;
            if (isCapturePossible(player)) {
                for (CheckersMove checkersMove : possible_capture_list) {
                    next_step = board.clone();
                    next_step.processMove(checkersMove);
                    max_state = !max_state;
                    double result = get_minimax_value(next_step, depth - 1, otherPlayer(player), max_state);

                    valueOfminimax = Math.min(result, valueOfminimax);
                }
            } else {
                for (CheckersMove checkersMove : possible_move_list) {
                    next_step = board.clone();
                    next_step.processMove(checkersMove);
                    max_state = !max_state;
                    double result = get_minimax_value(next_step, depth - 1, otherPlayer(player), max_state);

                    valueOfminimax = Math.min(result, valueOfminimax);
                }
            }

        }

        return valueOfminimax;
    }

    private CheckersMove minimaxMove(CheckersBoard board, int depth, Player player, boolean max_state) throws BadMoveException {

        List<CheckersMove> possible_capture_list = board.possibleCaptures(player);
        List<CheckersMove> possible_move_list = board.possibleMoves(player);

        List<Double> heuristics = new ArrayList<>();
        if (possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
            return null;
        }
        CheckersBoard nextStep = null;

        if (!possible_capture_list.isEmpty()) {
            for (CheckersMove checkersMove : possible_capture_list) {
                nextStep = board.clone();
                nextStep.processMove(checkersMove);
                max_state = !max_state;
                heuristics.add(get_minimax_value(nextStep, depth - 1, otherPlayer(player), max_state));
            }

        } else {
            for (CheckersMove checkersMove : possible_move_list) {
                nextStep = board.clone();
                nextStep.processMove(checkersMove);
                max_state = !max_state;
                heuristics.add(get_minimax_value(nextStep, depth - 1, otherPlayer(player), max_state));
            }
        }

        double best_heuristic = Double.MIN_VALUE;

        for (int i = heuristics.size() - 1; i >= 0; i--) {
            if (heuristics.get(i) >= best_heuristic) {
                best_heuristic = heuristics.get(i);
            }
        }
        CheckersMove move = null;
        for (int i = 0; i < heuristics.size(); i++) {
            if (heuristics.get(i) < best_heuristic) {
                heuristics.remove(i);
                if (isCapturePossible(player)) {
                    move = possible_capture_list.remove(i);
                } else {
                    move = possible_move_list.remove(i);
                }
                i--;
            }
        }

        return move;
    }
}
