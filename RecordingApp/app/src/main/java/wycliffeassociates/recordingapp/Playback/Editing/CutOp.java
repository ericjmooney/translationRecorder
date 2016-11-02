package wycliffeassociates.recordingapp.Playback.Editing;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import wycliffeassociates.recordingapp.AudioInfo;
import wycliffeassociates.recordingapp.Reporting.Logger;

/**
 * Created by sarabiaj on 12/22/2015.
 */
public class CutOp {
    private Vector<Pair<Integer, Integer>> mStack;
    private Vector<Pair<Integer, Integer>> mFlattenedStack;
    private int mSizeCut = 0;
    private Vector<Pair<Integer, Integer>> mCutStackUncmpLoc;
    private Vector<Pair<Integer, Integer>> mCutStackCmpLoc;
    private int mSizeCutCmp;
    private int mSizeCutUncmp;

    public CutOp() {
        mStack = new Vector<>();
    }

    public synchronized Vector<Pair<Integer,Integer>> getFlattenedStack(){
        return mFlattenedStack;
    }

    public synchronized void cut(int start, int end){
        Pair<Integer, Integer> temp = new Pair<>(start, end);
        mStack.add(temp);
        mSizeCut = totalDataRemoved();
        Logger.w(this.toString(), "Generating location stacks");
        generateCutStackUncmpLoc();
        generateCutStackCmpLoc();
    }

    public synchronized void clear(){
        mStack.clear();
        mFlattenedStack.clear();
        mSizeCut = 0;
        mCutStackCmpLoc.clear();
        mCutStackUncmpLoc.clear();
        mSizeCutCmp = 0;
        mSizeCutUncmp = 0;
    }

    public synchronized void undo(){
        if(mStack.size() == 0){
            return;
        }
        mStack.remove(mStack.size() - 1);
        mSizeCut = totalDataRemoved();
        generateCutStackUncmpLoc();
        generateCutStackCmpLoc();
    }

    //change to use flattened stack
    public synchronized int skip(int time){
        int max = -1;
        for (Pair<Integer, Integer> cut : mStack) {
            if (time >= cut.first && time < cut.second) {
                max = Math.max(cut.second, max);
            }
        }
        return max;
    }

    public synchronized boolean hasCut(){
        if(mStack.size() > 0){
            return true;
        } else {
            return false;
        }
    }

    public synchronized int skipReverse(int time){
        int min = Integer.MAX_VALUE;
        for (Pair<Integer, Integer> cut : mStack) {
            if (time > cut.first && time <= cut.second) {
                min = Math.min(cut.first, min);
            }
        }
        return min;
    }

    /**
     * Computes the total time removed from cutting, for use in ACTUAL capacity computations.
     * Also generates a private simplified and ordered version of this stack, eliminating nested
     * cuts.
     * <p/>
     * Begins by sorting the stack by starting cuts in a copied stack. Beginning with the earliest
     * cut, it is added to a list and removed from the stack. For each cut in this list (beginning
     * only with one cut) cuts are added if they start between the current cut's start and end. Each
     * cut that is added is removed from the stack. When the list has been iterated over, the min
     * and max are computed, which forms a new pair to be added to mFlattenedStack. The difference
     * between min and max are added to a sum. This process is repeated until the stack is empty.
     *
     * @return the total time removed from cuts.
     */
    private synchronized int totalDataRemoved(){
        Vector<Pair<Integer,Integer>> copy = new Vector<>(mStack.capacity());
        mFlattenedStack = new Vector<>();
        for (Pair<Integer, Integer> p : mStack) {
            copy.add(new Pair<>(p.first, p.second));
        }
        Collections.sort(copy, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public synchronized int compare(Pair<Integer, Integer> lhs, Pair<Integer, Integer> rhs) {
                if(lhs.first == rhs.first) {
                    return 0;
                } else if (lhs.first > rhs.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        int sum = 0;
        Logger.w(this.toString(), "Generating flattened stack and computing time removed");
        while (!copy.isEmpty()) {
            Pair<Integer, Integer> pair = copy.firstElement();
            ArrayList<Pair<Integer, Integer>> list = new ArrayList<>();
            list.add(pair);
            for(int i = 0; i < list.size(); i++){
                Pair<Integer,Integer> p =  list.get(i);
                copy.remove(p);
                for(int j = copy.size()-1; j >= 0; j--){
                    Pair<Integer,Integer> q = copy.get(j);
                    if((q.first >= p.first && q.first <= p.second) || (p.first >= q.first && p.first <= q.second)){
                        list.add(q);
                        copy.remove(q);
                    }
                }
            }
            int start = list.get(0).first;
            int end = list.get(0).second;
            for (int i = 1; i < list.size(); i++) {
                end = (end < list.get(i).second) ? list.get(i).second : end;
            }
            mFlattenedStack.add(new Pair<Integer, Integer>(start, end));
            sum += end - start;
        }
        return sum;
    }

    /**
     * Since the marker position takes into account total data played by audiotrack, the position
     * is agnostic of the "actual" position. This method computes the time to add back to it,
     * it takes the original time, looks to see if it's greater than or equal to a start cut. If so
     * it adds total time cut out, and adds this to time. Time is then compared to the next cut, and
     * the process is repeated. Break when the next cut takes place at a later time than we're at.
     * <p/>
     * mFlattenedStack is a representation of the cut stack WITHOUT any nested cuts, and based on
     * the way it is computed, we can assume this list is sorted.
     *
     * @param timeMs location that was computed from BufferPlayer before considering cuts
     * @return inflated time accounting for cuts
     */
    public synchronized int timeAdjusted(int timeMs){
        if(mFlattenedStack == null) {
            return timeMs;
        }
        int time = timeMs;
        for (Pair<Integer, Integer> p : mFlattenedStack) {
            if (time >= p.first) {
                time += p.second - p.first;
            } else {
                break;
            }
        }
        return time;
    }

    public synchronized int timeAdjusted(int timeMs, int playbackStart){
        if(mFlattenedStack == null) {
            return timeMs;
        }
        int time = timeMs;
        for (Pair<Integer, Integer> p : mFlattenedStack) {
            if (p.second > playbackStart) {
                if (time >= p.first) {
                    time += p.second - p.first;
                } else {
                    break;
                }
            }
        }
        return time;
    }

    /**
     * Given an absolute time in the uncut waveform, this method
     * returns the adjusted time in the cut waveform.  The given
     * time must not be in an existing cut.
     *
     * @param timeMs a time in ms in the uncut waveform.  timeMs must not
     *               be in an existing cut.
     * @return the adjusted time in the cut waveform.
     */
    public synchronized int reverseTimeAdjusted(int timeMs){
        if (mFlattenedStack == null) {
            return timeMs;
        }
        int time = timeMs;
        for (Pair<Integer, Integer> p : mFlattenedStack) {
            if (timeMs >= p.second) {
                time -= p.second - p.first;
            } else {
                break;
            }
        }

        return time;
    }

    private synchronized void generateCutStackUncmpLoc(){
        mSizeCutUncmp = 0;
        mCutStackUncmpLoc = new Vector<Pair<Integer, Integer>>();
        for (Pair<Integer, Integer> p : mFlattenedStack) {
            Pair<Integer, Integer> y = new Pair<>(timeToUncmpLoc(p.first), timeToUncmpLoc(p.second));
            mCutStackUncmpLoc.add(y);
            mSizeCutUncmp += y.second - y.first;
        }
    }

    private synchronized void generateCutStackCmpLoc(){
        mSizeCutCmp = 0;
        mCutStackCmpLoc = new Vector<Pair<Integer, Integer>>();
        for (Pair<Integer, Integer> p : mFlattenedStack) {
            Pair<Integer, Integer> y = new Pair<>(timeToCmpLoc(p.first), timeToCmpLoc(p.second));
            mCutStackCmpLoc.add(y);
            mSizeCutCmp += y.second - y.first;
        }
    }

    public synchronized int timeToUncmpLoc(int timeMs){
        int seconds = timeMs/1000;
        int ms = (timeMs-(seconds*1000));
        int tens = ms/10;


        int idx = (AudioInfo.SAMPLERATE * seconds) + (ms * 44) + (tens);
        //idx *= 2;
        return idx;
    }

    public synchronized int timeToCmpLoc(int timeMs){
        int seconds = timeMs/1000;
        int ms = (timeMs-(seconds*1000));
        int tens = ms/10;


        int idx = (AudioInfo.SAMPLERATE * seconds) + (ms * 44) + (tens);
        idx /= 25;
        //idx *= 2;
        return idx;
    }

    public synchronized int skipLoc(int loc, boolean compressed){
        int max = -1;
        Vector<Pair<Integer, Integer>> stack = (compressed) ? mCutStackCmpLoc : mCutStackUncmpLoc;
        for (Pair<Integer, Integer> cut : stack) {
            if (loc >= cut.first && loc < cut.second) {
                max = Math.max(cut.second, max);
            }
        }
        return max;
    }

    public synchronized int relativeLocToAbsolute(int loc, boolean compressed){
        Vector<Pair<Integer,Integer>> stack = (compressed)? mCutStackCmpLoc : mCutStackUncmpLoc;
        if(stack == null){
            return loc;
        }
        for (Pair<Integer, Integer> cut : stack) {
            if (loc >= cut.first) {
                loc += cut.second - cut.first;
            }
        }
        return loc;
    }

    public synchronized boolean cutExistsInRange(int position, int range){
        if(hasCut()) {
            for (Pair<Integer, Integer> cut : mCutStackUncmpLoc) {
                //if the position is in the middle of a cut
                if (position >= cut.first && position <= cut.second) {
                    return true;
                    //if the cut is between the position and the end of the range
                } else if (position < cut.first && (position + range) >= cut.second) {
                    return true;
                }
            }
        }
        //otherwise no cut
        return false;
    }


    public synchronized int absoluteLocToRelative(int location, boolean compressed){
        Vector<Pair<Integer,Integer>> stack = (compressed)? mCutStackCmpLoc : mCutStackUncmpLoc;
        int loc = location;
        if(stack == null){
            return loc;
        }
        for(int i = stack.size()-1; i >=0; i--) {
            Pair<Integer,Integer> cut = stack.get(i);
            if (location >= cut.second) {
                loc -= cut.second - cut.first;
            }
        }
        return loc;
    }

    public synchronized int getSizeCut(){
        return mSizeCut;
    }
    public synchronized int getSizeCutCmp(){
        return mSizeCutCmp;
    }
    public synchronized int getSizeCutUncmp(){
        return mSizeCutUncmp;
    }
}
