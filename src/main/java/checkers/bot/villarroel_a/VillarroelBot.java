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

//        int alpha = Integer.MIN_VALUE;
//        int beta = Integer.MAX_VALUE;
//        Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state, alpha, beta);

        Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state);
        return Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey(); // movement with maximum utility
    }

    private int get_heuristic(CheckersBoard board, Player player) { // The heuristic depends on the number of pieces on the board.

        int number_of_my_plain_pieces = get_correct_number_of_pieces_after_crowning(board, player) - getNumberOfQueens(board, player);
        int number_of_enemy_plain_pieces = get_correct_number_of_pieces_after_crowning(board, otherPlayer(player)) - getNumberOfQueens(board, otherPlayer(player));
        int number_of_pieces_that_can_move = board.possibleCaptures(player).size() + board.possibleMoves(player).size();
        int number_of_pieces_that_enemy_can_move = board.possibleCaptures(otherPlayer(player)).size() + board.possibleMoves(otherPlayer(player)).size();
        int number_of_pieces_that_cannot_move = board.countPiecesOfPlayer(player) - number_of_pieces_that_can_move;
        int number_of_pieces_that_enemy_cannot_move = board.countPiecesOfPlayer(otherPlayer(player)) - number_of_pieces_that_enemy_can_move;
//        int number_of_my_plain_pieces = board.countPiecesOfPlayer(player) - getNumberOfQueens(board, player);
//        int number_of_enemy_plain_pieces = board.countPiecesOfPlayer(otherPlayer(player)) - getNumberOfQueens(board, otherPlayer(player));

        return ((number_of_my_plain_pieces * 10)
                + (getNumberOfQueens(board, player) * 100)
                + (number_of_pieces_that_enemy_cannot_move * 5)
                + (number_of_pieces_that_can_move * 2)
                - (number_of_enemy_plain_pieces * 10)
                - (getNumberOfQueens(board, otherPlayer(player)) * 100)
                - (number_of_pieces_that_cannot_move * 5)
                - (number_of_pieces_that_enemy_can_move * 2)

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

        possible_capture_list.stream().filter(capture -> !isNotMyPiece(capture.getStartRow(), capture.getStartCol())).forEach(possible_capture_list::remove);
        possible_move_list.stream().filter(move -> !isNotMyPiece(move.getStartRow(), move.getStartCol())).forEach(possible_move_list::remove);

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


    private int get_correct_number_of_pieces_after_crowning(CheckersBoard board, Player current_player) {
        return board.countPiecesOfPlayer(current_player) - getNumberOfQueens(board, otherPlayer(current_player));
    }

    public int getNumberOfQueens(CheckersBoard board, Player player) {

        int numberOfQueens = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((board.getBoard()[i][j] == RED_CROWNED && player == Player.RED)
                        || (board.getBoard()[i][j] == BLACK_CROWNED && player == Player.BLACK)) {
                    numberOfQueens++;
                }
            }
        }

        return numberOfQueens;
    }

}
