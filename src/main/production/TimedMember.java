package main.production;


import java.util.*;

public class TimedMember {

    int field;
    long lastPingTime;
    Timer timer;
    TimedMember me;
    Set<TimedMember> set = null;
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
    public TimedMember(int f, long time, long timeToLive){
        this(f, time);
        TTL = timeToLive;
    }
    public TimedMember(int f, long time){
        field = f;
        lastPingTime = time;
        me = this;
        TTL = 1000;
    }
    public TimedMember(int f){
        this(f, new Date().getTime());
    }

    public void addToSet(Set<TimedMember> set_){
        set=set_;
        if (set.add(this)){
            timer = new Timer(true);
            timer.schedule(myTask, TTL, TTL);
        }
    }


    @Override
    public String toString(){
        return "f=" + field + ";LPT=" + lastPingTime + ";TWP=" +
                (double)(new Date().getTime() - lastPingTime)/1000;
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
        if(this.field != other.field) return false;

        other.lastPingTime = lastPingTime = Math.max(lastPingTime, other.lastPingTime);
        return true;
    }
}
