package gameClient;

import api.*;
import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *  this class represent an instance of a single agent inside the game
 */
public class CL_Agent {
    public static final double EPS = 0.0001;
    private static int _count = 0;
    private static int _seed = 3331;
    private int _id;
    private geo_location _pos;
    private double _speed;
    private edge_data _curr_edge;
    private node_data _curr_node;
    public int counter;
    private directed_weighted_graph _gg;
    private CL_Pokemon _curr_fruit;
    private long _sg_dt;
    private double _value;
    private List<node_data> path;
    private String lastEaten;

    public List<node_data> getPath() {
        return path;
    }

    public String getLastEaten() {
        return lastEaten;
    }

    public void setLastEaten(String lastEaten) {
        this.lastEaten = lastEaten;
    }


    public void setPath(List<node_data> path, node_data n) {
        this.path = path;
        path.add(n);
    }

    public CL_Agent(directed_weighted_graph g, int start_node) {
        counter = 1;
        _gg = g;
        setMoney(0);
        this._curr_node = _gg.getNode(start_node);
        _pos = _curr_node.getLocation();
        _id = -1;
        path = new LinkedList<>();
        setSpeed(0);
    }

    /**
     * update the current agent in refrence to the status of the game and server
     * , it receive the current agent status via the server in JSON format and translate it to an agent object.
     * @param json
     */
    public void update(String json) {
        JSONObject line;
        try {
            line = new JSONObject(json);
            JSONObject ttt = line.getJSONObject("Agent");
            int id = ttt.getInt("id");
            if (id == this.getID() || this.getID() == -1) {
                if (this.getID() == -1) {
                    _id = id;
                }
                double speed = ttt.getDouble("speed");
                String p = ttt.getString("pos");
                Point3D pp = new Point3D(p);
                int src = ttt.getInt("src");
                int dest = ttt.getInt("dest");
                double value = ttt.getDouble("value");
                this._pos = pp;
                this.setCurrNode(src);
                this.setSpeed(speed);
                this.setNextNode(dest);
                this.setMoney(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSrcNode() {
        return this._curr_node.getKey();
    }

    /**
     * turns the current agent's status and values to JSON.
     * @return
     */
    public String toJSON() {
        int d = this.getNextNode();
        return "{\"Agent\":{"
                + "\"id\":" + this._id + ","
                + "\"value\":" + this._value + ","
                + "\"src\":" + this._curr_node.getKey() + ","
                + "\"dest\":" + d + ","
                + "\"speed\":" + this.getSpeed() + ","
                + "\"pos\":\"" + _pos.toString() + "\""
                + "}"
                + "}";
    }

    private void setMoney(double v) {
        _value = v;
    }

    public boolean setNextNode(int dest) {
        boolean ans = false;
        int src = this._curr_node.getKey();
        this._curr_edge = _gg.getEdge(src, dest);
        if (_curr_edge != null) {
            ans = true;
        }
        return ans;
    }

    public void setCurrNode(int src) {
        this._curr_node = _gg.getNode(src);
    }

    public boolean isMoving() {
        return this._curr_edge != null;
    }

    public String toString() {
        return toJSON();
    }
    public int getID() {

        return this._id;
    }

    public geo_location getLocation() {
        return _pos;
    }

    public double getValue() {
        return this._value;
    }

    public int getNextNode() {
        int ans;
        if (this._curr_edge == null) {
            ans = -1;
        } else {
            ans = this._curr_edge.getDest();
        }
        return ans;
    }

    public double getSpeed() {
        return this._speed;
    }

    public void setSpeed(double v) {
        this._speed = v;
    }

    public CL_Pokemon get_curr_fruit() {
        return _curr_fruit;
    }

    public void set_curr_fruit(CL_Pokemon curr_fruit) {
        this._curr_fruit = curr_fruit;
    }

    public void set_curr_edge(edge_data _curr_edge) {
        this._curr_edge = _curr_edge;
    }

    /**
     *  a function which have a getter and setter , it receive a long and pokemon
     *  and if the said pokemon is a pokemon in which the agent is having a hard time to eat
     *  , SDT caculates the new number to send to the server so the agent will succeed to eat the pokemon.
     * @param ddtt
     * @param k
     */
    public void set_SDT(long ddtt, CL_Pokemon k) {
        long ddt = ddtt;
        if (this._curr_edge != null) {
            double w = get_curr_edge().getWeight();
            geo_location dest = _gg.getNode(get_curr_edge().getDest()).getLocation();
            geo_location src = _gg.getNode(get_curr_edge().getSrc()).getLocation();
            double de = src.distance(dest);
            double dist = _pos.distance(dest);
            if (k.get_edge() == this.get_curr_edge()) {
                dist = k.getLocation().distance(this._pos);
            }
            double norm = dist / de;
            double dt = w * norm / (this.getSpeed() * counter);
            ddt = (long) (1000.0 * dt);
            if (ddt < 35)
                ddt = 35;
        }
        this.set_sg_dt(ddt);
    }

    public edge_data get_curr_edge() {
        return this._curr_edge;
    }

    public long get_sg_dt() {
        return _sg_dt;
    }

    public void set_sg_dt(long _sg_dt) {
        this._sg_dt = _sg_dt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CL_Agent agent = (CL_Agent) o;
        return _id == agent._id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}
