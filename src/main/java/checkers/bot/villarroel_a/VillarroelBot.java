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

        try {
            Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, current_player, false);
//            utility_list_map.entrySet().forEach(e -> System.out.println("S=" + e.getKey() + "  A=" + e.getValue()));

            best_move = Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey(); // movement with maximum utility
        } catch (BadMoveException e) {
            e.printStackTrace();
        }

        return best_move;
    }

    private int get_heuristic(CheckersBoard board, Player player) {

        return ((get_number_of_plain_pieces(board, player) * 50)
                + (get_number_of_queens(board, player) * 200)
                + (get_number_of_pieces_that_cannot_move(board, otherPlayer(player)) * 10)
                + (get_number_of_pieces_that_can_move(board, player) * 5)
                + (get_possibility_to_capture(board))
                - (get_number_of_plain_pieces(board, otherPlayer(player)) * 50)
                - (get_number_of_queens(board, otherPlayer(player)) * 200)
                - (get_number_of_pieces_that_cannot_move(board, player) * 10)
                - (get_number_of_pieces_that_can_move(board, otherPlayer(player)) * 5)
        );

    }

    private int get_utility_value(CheckersBoard board, int depth, Player player, boolean is_max_state, int alpha, int beta) throws BadMoveException {

        if (depth == 0) {
            return get_heuristic(board, player);
        }

        Map<CheckersMove, Integer> utility_list_map = get_move_and_utility_for_successors_list(board, depth, player, is_max_state);

        if (!utility_list_map.isEmpty()) {
            if (is_max_state) {
                int value = Collections.max(utility_list_map.values());
                alpha = Math.max(alpha, value);
                return alpha;
            } else {
                int value = Collections.min(utility_list_map.values());
                beta = Math.min(beta, value);
                return beta;
            }
        }
        return 0;
    }

    public Map<CheckersMove, Integer> get_move_and_utility_for_successors_list(CheckersBoard board, int depth, Player player, boolean is_max_state) throws BadMoveException {

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        Map<CheckersBoard, CheckersMove> successors = generate_successors(board);
        Map<CheckersMove, Integer> utility_and_move_list_map = new HashMap<>();

        if (successors != null) {
            for (Map.Entry<CheckersBoard, CheckersMove> entry : successors.entrySet()) {
                CheckersBoard successor_board = entry.getKey();
                CheckersMove successor_move = entry.getValue();
                is_max_state = !is_max_state;
                utility_and_move_list_map.put(successor_move, get_utility_value(successor_board, depth - 1, otherPlayer(player), is_max_state, alpha, beta));
                is_max_state = !is_max_state;
            }
        }

        return utility_and_move_list_map;
    }


    private Map<CheckersBoard, CheckersMove> generate_successors(CheckersBoard board) throws BadMoveException {

        List<CheckersMove> move_list = possible_action(board);

        if (move_list.isEmpty()) {
            return null;
        }

        Map<CheckersBoard, CheckersMove> successors = new HashMap<>();

        for (CheckersMove possible_move : move_list) {
            CheckersBoard new_state = board.clone();
            new_state.processMove(possible_move);
            successors.put(new_state, possible_move);
        }

        return successors;
    }


    public List<CheckersMove> possible_action(CheckersBoard board) {

        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

        if (possible_capture_list.isEmpty()) {
            return possible_move_list;
        }
        return possible_capture_list;

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

    private int count_pieces_for_list(List<CheckersMove> move_list) {
        int number_of_pieces = 0;
        int i = 0;
        int j = 0;

        for (CheckersMove move : move_list) {
            if (move.getStartRow() != i || move.getStartCol() != j) {
                number_of_pieces++;
                i = move.getStartRow();
                j = move.getStartCol();
            }
        }
        return number_of_pieces;
    }

    private int get_number_of_pieces_that_can_move(CheckersBoard board, Player player) {
        return count_pieces_for_list(board.possibleMoves(player)) + count_pieces_for_list(board.possibleCaptures(player));
    }

    private int get_possibility_to_capture(CheckersBoard board) {
        if (!board.isCaptureLock()) {
            return -25;
        }
        return 25;
    }

    private int get_number_of_pieces_that_cannot_move(CheckersBoard board, Player player) {
        return get_correct_number_of_pieces_after_crowning(board, player) - get_number_of_pieces_that_can_move(board, player);
    }

}
