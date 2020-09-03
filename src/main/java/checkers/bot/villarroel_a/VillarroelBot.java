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
        CheckersMove best_move = null;

        try {
          best_move = get_best_Move(board, depth, current_player, true);

        } catch (BadMoveException e) {
            e.printStackTrace();
        }

        return best_move;
    }

    private CheckersMove get_best_Move(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {

        Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state);

        if (!board.isCapturePossible() && !board.isMovePossible()) {
            return null;
        }

        return Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey(); // movement with maximum utility
    }

    private int get_heuristic(CheckersBoard board, Player player) { // The heuristic depends on the number of pieces on the board.

        int plain_cost = 10;
        int queen_cost = 100;

        int number_of_my_plain_pieces = board.countPiecesOfPlayer(player) - getNumberOfQueens(board, player);
        int number_of_enemy_plain_pieces = board.countPiecesOfPlayer(otherPlayer(player)) - getNumberOfQueens(board, otherPlayer(player));

        return ((number_of_my_plain_pieces * plain_cost) + (getNumberOfQueens(board, player) * queen_cost)
                - (number_of_enemy_plain_pieces * plain_cost) - (getNumberOfQueens(board, otherPlayer(player)) * queen_cost)
        );

    }

    public int getNumberOfQueens(CheckersBoard board, Player player) {

        int numberOfqueens = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((board.getBoard()[i][j] == RED_CROWNED && player == Player.RED)
                        || (board.getBoard()[i][j] == BLACK_CROWNED && player == Player.BLACK)) {
                    numberOfqueens++;
                }
            }
        }

        return numberOfqueens;
    }

    private int get_utility_value(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {

        if (depth == 0) {
            return get_heuristic(board, player);
        }

        int minimax_value = 0;

        Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state);

        if(!utility_list_map.isEmpty()) {
            if (is_max_state) {
                minimax_value = Collections.max(utility_list_map.values());
            } else {
                minimax_value = Collections.min(utility_list_map.values());
            }
        }

        return minimax_value;
    }

    public Map<CheckersMove, Integer> get_move_and_utility_for_successors_list(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {

        Map<CheckersBoard, CheckersMove> successors = generate_successors(board);
        Map<CheckersMove, Integer> utility_and_move_list_map = new HashMap<>();

        if (successors != null) {
            for (Map.Entry<CheckersBoard, CheckersMove> entry : successors.entrySet()) {
                CheckersBoard successor_board = entry.getKey();
                CheckersMove successor_move = entry.getValue();
                is_max_state = !is_max_state;
                utility_and_move_list_map.put(successor_move, get_utility_value(successor_board, depth - 1, otherPlayer(player), is_max_state));
            }
        }

        return utility_and_move_list_map;
    }

    private Map<CheckersBoard, CheckersMove> generate_successors(CheckersBoard board) throws BadMoveException {

        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

//        possible_capture_list.stream().filter(capture -> !isNotMyPiece(capture.getStartRow(), capture.getStartCol())).forEach(possible_capture_list::remove);
//        possible_move_list.stream().filter(move -> !isNotMyPiece(move.getStartRow(), move.getStartCol())).forEach(possible_move_list::remove);

        if (possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
            return null;
        }

        Map<CheckersBoard, CheckersMove> successors = new HashMap<>();

        if (!possible_capture_list.isEmpty()) {
            for (CheckersMove checkers_capture : possible_capture_list) {
                CheckersBoard new_state = board.clone();
                new_state.processMove(checkers_capture);
                successors.put(new_state, checkers_capture);
            }
        } else {
            for (CheckersMove checkers_move : possible_move_list) {
                CheckersBoard new_state = board.clone();
                new_state.processMove(checkers_move);
                successors.put(new_state, checkers_move);

            }
        }

        return successors;
    }


}
