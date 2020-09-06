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
        int depth = 6;

        CheckersMove best_move = null;
//        int alpha = Integer.MIN_VALUE;
//        int beta = Integer.MAX_VALUE;

        try {
            //        Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state, alpha, beta);
            Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, current_player, true);
            best_move = Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey(); // movement with maximum utility
        } catch (BadMoveException e) {
            e.printStackTrace();
        }

        return best_move;
    }

    private int get_heuristic(CheckersBoard board, Player player) { // The heuristic depends on the number of pieces on the board.

//        int number_of_my_plain_pieces = board.countPiecesOfPlayer(player) - getNumberOfQueens(board, player);
//        int number_of_enemy_plain_pieces = board.countPiecesOfPlayer(otherPlayer(player)) - getNumberOfQueens(board, otherPlayer(player));

        return ((get_number_of_plain_pieces(board, player) * 10)
                + (get_number_of_queens(board, player) * 100)
                + (get_number_of_pieces_that_cannot_move(board, otherPlayer(player)) * 5)
                + (get_number_of_pieces_that_can_move(board, player) * 2)
                - (get_number_of_plain_pieces(board, otherPlayer(player)) * 10)
                - (get_number_of_queens(board, otherPlayer(player)) * 100)
                - (get_number_of_pieces_that_cannot_move(board, player) * 5)
                - (get_number_of_pieces_that_can_move(board, otherPlayer(player)) * 2)

        );

    }

    //    private int get_utility_value(CheckersBoard board, int depth, Player player, boolean is_max_state, int alpha, int beta) throws BadMoveException {
        private int get_utility_value(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {

        if (depth == 0) {
            return get_heuristic(board, player);
        }

        int minimax_value = 0;

//        Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state, alpha, beta);
        Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state);

        if (!utility_list_map.isEmpty()) {
            if (is_max_state) {
                minimax_value = Collections.max(utility_list_map.values());
//                alpha = Math.max(alpha, value);
//                if(alpha >= beta){
//                    minimax_value = beta;
//                }
            } else {
                minimax_value = Collections.min(utility_list_map.values());
//                beta = Math.min(alpha, value);
//                if(beta <= alpha){
//                    minimax_value = alpha;
//                }
            }
        }

        return minimax_value;
    }

    public Map<CheckersMove, Integer> get_move_and_utility_for_successors_list(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {
//    public Map<CheckersMove, Integer> get_move_and_utility_for_successors_list(CheckersBoard board, int depth, Player player, boolean is_max_state, int alpha, int beta) throws BadMoveException {

        Map<CheckersBoard, CheckersMove> successors = generate_successors(board);
        Map<CheckersMove, Integer> utility_and_move_list_map = new HashMap<>();

        if (successors != null) {
            for (Map.Entry<CheckersBoard, CheckersMove> entry : successors.entrySet()) {
                CheckersBoard successor_board = entry.getKey();
                CheckersMove successor_move = entry.getValue();
                is_max_state = !is_max_state;
                utility_and_move_list_map.put(successor_move, get_utility_value(successor_board, depth - 1, otherPlayer(player), is_max_state));
//                utility_and_move_list_map.put(successor_move, get_utility_value(successor_board, depth - 1, otherPlayer(player), is_max_state, alpha, beta));
            }
        }

        return utility_and_move_list_map;
    }

    private Map<CheckersBoard, CheckersMove> generate_successors(CheckersBoard board) throws BadMoveException {

        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

//        possible_capture_list.stream().filter(capture -> !isNotMyPiece(capture.getStartRow(), capture.getStartCol())).forEach(possible_capture_list::remove);
//        possible_move_list.stream().filter(move -> !isNotMyPiece(move.getStartRow(), move.getStartCol())).forEach(possible_move_list::remove);
//        possible_capture_list.stream().filter(capture -> (ownerOf(capture.getStartRow(), capture.getStartCol())).equals(board.getCurrentPlayer()));

        if (possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
            return null;
        }

        Map<CheckersBoard, CheckersMove> successors = new HashMap<>();

        if (!possible_capture_list.isEmpty()) {
            for (CheckersMove possible_capture : possible_capture_list) {
                CheckersBoard new_state = board.clone();
                new_state.processMove(possible_capture);
                successors.put(new_state, possible_capture);
            }
        } else {
            for (CheckersMove possible_move : possible_move_list) {
                CheckersBoard new_state = board.clone();
                new_state.processMove(possible_move);
                successors.put(new_state, possible_move);
            }
        }

        return successors;
    }

    public int get_number_of_queens(CheckersBoard board, Player player) {

        int number_of_queens = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((board.getBoard()[i][j] == RED_CROWNED && player == Player.RED)
                        || (board.getBoard()[i][j] == BLACK_CROWNED && player == Player.BLACK)) {
                    number_of_queens++;
                }
            }
        }

        return number_of_queens;
    }

    private int get_correct_number_of_pieces_after_crowning(CheckersBoard board, Player player) {
        return board.countPiecesOfPlayer(player) - get_number_of_queens(board, otherPlayer(player));
    }

    private int get_number_of_plain_pieces(CheckersBoard board, Player player) {
        return get_correct_number_of_pieces_after_crowning(board, player) - get_number_of_queens(board, player);
    }

    private int get_number_of_pieces_that_can_move(CheckersBoard board, Player player) {
        return board.possibleCaptures(player).size() + board.possibleMoves(player).size();
    }

    private int get_number_of_pieces_that_cannot_move(CheckersBoard board, Player player) {
        return get_correct_number_of_pieces_after_crowning(board, player) - get_number_of_pieces_that_can_move(board, player);
    }

}
