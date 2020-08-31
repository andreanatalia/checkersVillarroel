package checkers.bot.villarroel_a;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;

import java.util.*;
import java.util.stream.Stream;


public class VillarroelBot extends CheckersBoard implements CheckersPlayer {

  /*  public VillarroelBot(){
        super();
    }*/

    @Override
    public CheckersMove play(CheckersBoard board) {
        List<CheckersMove> possible_capture_list = board.possibleCaptures();
        List<CheckersMove> possible_move_list = board.possibleMoves();

        int maxUtility = Integer.MIN_VALUE;
        CheckersMove best_option = null;

        if (possible_capture_list.isEmpty()) {

            for (CheckersMove move_successor : possible_move_list) {
                int utility = getUtility(move_successor,board);
                if (maxUtility < utility) {
                    maxUtility = utility;
                    best_option = move_successor;
                }
            }

        }else{
            for (CheckersMove capture_successor : possible_capture_list) {
                int utility = getUtility(capture_successor,board);
                if (maxUtility < utility) {
                    maxUtility = utility;
                    best_option = capture_successor;
                }
            }
        }
        return best_option;
//        for (CheckersMove possible_capture : possible_capture_list) {
//            getUtility(state, board);
//
    }

    public boolean enemyCannotMoveOrCapture() {
        // quickly switch over to the other player
        // to check their possible moves/captures
        switchTurn();
        boolean enemyCannotMoveOrCapture = false;
        if (!isMovePossible() && !isCapturePossible()) {
            enemyCannotMoveOrCapture = true;
        }
        // switch the turn back to the real "current player"
        switchTurn();
        return enemyCannotMoveOrCapture;
    }

    public int getUtility(CheckersMove successor, CheckersBoard board) {
        List<CheckersMove> possible_capture_list = possibleCaptures();
        List<CheckersMove> possible_move_list = possibleMoves();
//        Map<CheckersMove, Integer> utilities;
/* if (possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
            if ( !enemyCannotMoveOrCapture() ) {
                if ( !isMovePossible() && !isCapturePossible() ) {
                    return -1;
                } else {
                    return 1;
              */
        if (possible_capture_list.isEmpty() && possible_move_list.isEmpty()) {
            if ( !successor.equals(Optional.empty()) ) {
                if ( enemyCannotMoveOrCapture() ) {
                    return 1;
                } else {
                    return -1;
                }
            }
            return 0;

        }
        Stream<Integer> utilities;

        if (possible_capture_list.isEmpty()) {
            utilities = possible_move_list.stream()//
                    .map(move_successor -> getUtility(successor, board));
        } else {
            utilities = possible_capture_list.stream()//
                    .map(capture_successor -> getUtility(successor, board));
        }

        if (otherPlayer() == Player.BLACK) {
            return utilities.max(Integer::compareTo).orElseThrow();

        } else {
            return utilities.min(Integer::compareTo).orElseThrow();
        }
//        utilities = new HashMap<>();
//        possible_capture_list.forEach(capture_child -> utilities.put(capture_child, getUtility(player,board)));

    }


}
