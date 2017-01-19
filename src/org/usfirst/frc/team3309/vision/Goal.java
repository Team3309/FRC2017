/*
 * Copyright 2016 Vinnie Magro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.usfirst.frc.team3309.vision;

/**
 * Created by vmagro on 1/17/16.
 */
public class Goal {

    /**
     * X and Y coordinates of target in the targeting coordinate system of [-1, 1], [-1, 1],
     * where (-1, 1) is the top left of the camera's viewable region, and (1, -1) is the bottom right
     */
    public final double x, y;

    /**
     * Width and height of the target as a fraction of the total image width/height
     */
    public final double width, height;

    /**
     * Distance to the target in inches
     */
    public final double distance;

    /**
     * Angle of elevation to target in degrees
     */
    public final double elevationAngle;

    /**
     * Azimuth angle (horizontal angle off of North) in degrees
     */
    public final double azimuth;

    @Override
    public String toString() {
        return "Goal{" +
                "center=(" + x + "," + y + ")" +
                ", size=(" + width + "," + height + ")" +
                ", distance=" + distance +
                ", elevation=" + elevationAngle + ", azimuth=" + azimuth
                 + '}';
    }

    public Goal(double x, double y, double width, double height, double distance, double elevationAngle, double azimuth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.distance = distance;
        this.elevationAngle = elevationAngle;
        this.azimuth = azimuth;
    }
}
