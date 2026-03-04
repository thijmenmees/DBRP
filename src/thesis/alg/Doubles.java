package thesis.alg;
public interface Doubles {
    void setNodeDoubles(Double[] pi);
    void setVanDoubles(Double[] sigma);
    Double[] getNodeDoubles();
    Double[] getVanDoubles();
    Double[] copyNodeDoubles();
    Double[] copyVanDoubles();
}
