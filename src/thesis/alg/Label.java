package thesis.alg;

public class Label {
    public int    node;
    public double time; // in hours
    public double rc;
    public int    load;
    public Label  prev;
    public long[] visited;
    public Label(int node, double time, double rc, int load, Label previous) {
        this.node = node;
        this.time = time;
        this.rc   = rc;
        this.load = load;
        this.prev = previous;
        this.visited = previous.visited.clone();
        setVisited();
    }
    public Label(int node, double time, double rc, int load) {
        this.node = node;
        this.time = time;
        this.rc   = rc;
        this.load = load;
        this.prev = null;
        this.visited = new long[(thesis.sim.Parameters.numHubs - 1) / 64 + 1];
    }

    public boolean dominates(Label other) {
        return (this.time <= other.time  &&
                this.rc   >= other.rc    &&
                this.load >= other.load);
    }
    public boolean stronglyDominates(Label other) {
        return (this.time <= other.time  &&
                this.rc   >= other.rc    &&
                this.load >= other.load) &&
               (this.time <  other.time  ||
                this.rc   >  other.rc    ||
                this.load >  other.load);
    }
    public boolean isFeasible() {
        return (time < Parameters.labelHorizon &&
                load > 0 &&
                load < Parameters.vanCapacity);
    }
    private void setVisited() {
        int idx = node >> 6;
        int bit = node & 63;
        visited[idx] |= (1L << bit);
    }
    public boolean isVisited(int node) {
        int idx = node >> 6;       // node / 64
        int bit = node & 63;       // node % 64
        return (visited[idx] & (1L << bit)) != 0;
    }
}
