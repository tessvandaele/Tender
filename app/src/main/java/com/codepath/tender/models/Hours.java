package com.codepath.tender.models;

public class Hours {

    private Open[] open;
    private boolean is_open_now;

    public Open[] getOpen() { return open; }

    public boolean isIs_open_now() { return is_open_now; }

    public void setOpen(Open[] open) { this.open = open; }

    public void setIs_open_now(boolean is_open_now) { this.is_open_now = is_open_now; }
}
