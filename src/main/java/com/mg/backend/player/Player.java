package com.mg.backend.player;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

    private final String id;
    private final String firstName;
    private final String lastName;
    private final String club;
    private final int shirtNumber;
    private final Position position;

    @JsonCreator
    public Player(@JsonProperty("id") String id,
                  @JsonProperty("firstName") String firstName,
                  @JsonProperty("lastName") String lastName,
                  @JsonProperty("club") String club,
                  @JsonProperty("shirtNumber") int shirtNumber,
                  @JsonProperty("position") Position position
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.shirtNumber = shirtNumber;
        this.position = position;
    }

  public String getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getClub() {
    return club;
  }

  public int getShirtNumber() {
    return shirtNumber;
  }

  public Position getPosition() {
    return position;
  }
}
