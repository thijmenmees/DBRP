package thesis.alg;
public class Doubles1 implements Doubles {
    private Double[] nodeDoubles;
    private Double[] vanDoubles;
    @Override
    public void setNodeDoubles(Double[] pi) {
        nodeDoubles = pi;
    }

    @Override
    public void setVanDoubles(Double[] sigma) {
        vanDoubles = sigma;
    }

    @Override
    public Double[] getNodeDoubles() {
        return nodeDoubles;
    }

    @Override
    public Double[] getVanDoubles() {
        return vanDoubles;
    }

    @Override
    public Double[] copyNodeDoubles() {
        return nodeDoubles.clone();
    }

    @Override
    public Double[] copyVanDoubles() {
        return vanDoubles.clone();
    }
}
