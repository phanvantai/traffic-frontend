package com.gemvietnam.trafficgem.library;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quannv on 5/3/2017.
 */

public class Point {
  private double lat;
  private double lon;
  private int color;
  private List<LatLng> peaks;

  public Point() {
  }

  public Point(double lat, double lon, int color) {
    this.lat = lat;
    this.lon = lon;
    this.color = color;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public List<LatLng> getPeaks(){
    peaks = new ArrayList<>();
    double delLat = 0.0089573;
    double delLon = 0.0088355;
    LatLng first = new LatLng(lat, lon);
    LatLng second = new LatLng(lat, lon + delLon);
    LatLng third = new LatLng(lat - delLat, lon);
    LatLng fourd = new LatLng(lat - delLat, lon + delLon);
    peaks.add(first);
    peaks.add(second);
    peaks.add(fourd);
    peaks.add(third);
    return peaks;
  }
}
