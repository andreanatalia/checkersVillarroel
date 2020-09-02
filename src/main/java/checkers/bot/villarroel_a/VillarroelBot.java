package checkers.bot.villarroel_a;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;
import checkers.exception.BadMoveException;

import java.util.*;
import java.util.stream.Stream;

public class VillarroelBot extends CheckersBoard implements CheckersPlayer {

    private Player current_player;
    boolean is_a_max_state = true;
    private List<CheckersMove> possible_capture_list;
    private List<CheckersMove> possible_move_list;

//    private List<CheckersMove> enemy_possible_capture_list;
//    private List<CheckersMove> enemy_possible_move_list;

    private int depth;

    public VillarroelBot() {
        super();
    }


    @Override
    public CheckersMove play(CheckersBoard board) {

        current_player = board.getCurrentPlayer();

        possible_capture_list = board.possibleCaptures();
        possible_move_list = board.possibleMoves();
//        enemy_possible_move_list = board.possibleCaptures(otherPlayer(current_player));
//        enemy_possible_move_list = board.possibleCaptures(otherPlayer(current_player));

//        possible_capture_list.forEach(capt->System.out.println("Possible Capture " + capt.toString()));
//        board.possibleCaptures(otherPlayer(current_player)).forEach(capt->System.out.println("-> Possible Capture Other Player " + capt.toString()));
//        possible_move_list.forEach(capt->System.out.println("Possible Move " + capt.toString() ));
//        board.possibleMoves(otherPlayer(current_player)).forEach(capt->System.out.println("-> Possible Move Other Player" + capt.toString() ));

        double maxUtility = Double.MIN_VALUE;
        int depth = 7;
        CheckersMove best_option = null;
//        List<CheckersBoard> successors;
        try {
            best_option =  get_utility_Move(board, depth);

        } catch (BadMoveException e) {
            e.printStackTrace();
        }

        return best_option;

    }

//       System.out.println("PIEZAS NEGRAS : " + board.countPiecesOfPlayer(current_player));
//       System.out.println("PIEZAS ROJAS : " + board.countPiecesOfPlayer(otherPlayer(current_player)));

    private  CheckersMove get_utility_Move(CheckersBoard board,  int depth) throws BadMoveException {

        if(!possible_capture_list.isEmpty() && !possible_move_list.isEmpty()){
            return null;
        }

        CheckersBoard next_state = null;
        double utility_value = get_utility_value(board, depth);
        Map<CheckersBoard, Double> utility_list_map = get_utility_for_successors_list(board, depth);

        CheckersBoard max_state = Collections.max(utility_list_map.entrySet(), Map.Entry.comparingByValue()).getKey();
        CheckersMove move= null;
        System.out.println(utility_list_map.containsValue(utility_value));

        if(!possible_capture_list.isEmpty()){
            for(CheckersMove possible_capture :  possible_capture_list){
               next_state = board.clone();
               next_state.processMove(possible_capture);
               if(next_state == max_state){
                   return possible_capture;
               }
            }
        }else{
            if(!possible_move_list.isEmpty()){
                for(CheckersMove possible_move :  possible_move_list){
                    next_state = board.clone();
                    next_state.processMove(possible_move);
                    if(next_state == max_state){
                        return possible_move;
                    }
                }
            }
        }
        return null;
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


    private double get_utility_value(CheckersBoard board, int depth) throws BadMoveException {

        if (depth == 0) {
            return get_heuristic(board);
        }

        double valueOfminimax = 0;


        Map<CheckersBoard, Double> utility_list_map = get_utility_for_successors_list(board, depth);
        System.out.println(utility_list_map.isEmpty());

        if (is_a_max_state) {
            valueOfminimax = Collections.max(utility_list_map.values());
        } else {
            valueOfminimax = Collections.min(utility_list_map.values());
        }
        is_a_max_state = !is_a_max_state;

        return valueOfminimax;
    }

    public Map<CheckersBoard, Double> get_utility_for_successors_list(CheckersBoard board, int depth) throws BadMoveException {

        List<CheckersBoard> successors = generate_successors(board);
        Map<CheckersBoard, Double> utility_list_map = new HashMap<>();

        for (CheckersBoard successor : successors) {
            utility_list_map.put(successor, get_utility_value(successor, depth - 1));
        }

        return utility_list_map;
    }

    private List<CheckersBoard> generate_successors(CheckersBoard board) throws BadMoveException {

        if (possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
            return Collections.emptyList();
        }

        CheckersBoard new_state = null;

        List<CheckersBoard> successors = new ArrayList<>();

        if (!possible_capture_list.isEmpty()) {
            for (CheckersMove checkers_capture : possible_capture_list) {
                new_state = board.clone();
                new_state.processMove(checkers_capture);
                successors.add(new_state);
            }
        } else {
            for (CheckersMove checkers_move : possible_move_list) {
                new_state = board.clone();
                new_state.processMove(checkers_move);
                successors.add(new_state);
            }
        }
        return successors;
    }
}
