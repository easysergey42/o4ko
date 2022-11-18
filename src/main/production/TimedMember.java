package main.production;


import java.util.*;

public class TimedMember<T> {

    T field;
    long lastPingTime;
    Timer timer;
    TimedMember<T> me;
    Set<TimedMember<T>> set = null;
    long TTL;
    TimerTask myTask = new TimerTask() {

        @Override
        public void run() {
            long withoutPing = new Date().getTime() - lastPingTime;
            if (withoutPing > TTL) {
//                System.out.println("Field " + field + " is removed :( | Time without ping = " + (double)withoutPing / 1000);
                set.remove(me);
                this.cancel();
            }
            else {
//                System.out.println("Field " + field + " is lucky | Time without ping = " + (double)withoutPing / 1000);
//                    this.cancel();
            }
        }
    };
    public TimedMember(T f, long time, long timeToLive){
        this(f, time);
        TTL = timeToLive;
    }
    public TimedMember(T f, long time){
        field = f;
        lastPingTime = time;
        me = this;
        TTL = 1000;
    }
    public TimedMember(T f){
        this(f, new Date().getTime());
    }

    public void addToSet(Set<TimedMember<T>> set_){
        set=set_;
        if (set.add(this)){
            timer = new Timer(true);
            timer.schedule(myTask, TTL, TTL);
        }
    }


    @Override
    public String toString(){
        return "" + field/* + ";LPT=" + lastPingTime + ";TWP=" +
                (double)(new Date().getTime() - lastPingTime)/1000*/;
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(field);
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (!(obj instanceof TimedMember)) return false;
        final TimedMember other = (TimedMember) obj;
        if(!this.field.equals(other.field)) return false;

        other.lastPingTime = lastPingTime = Math.max(lastPingTime, other.lastPingTime);
        return true;
    }
}
