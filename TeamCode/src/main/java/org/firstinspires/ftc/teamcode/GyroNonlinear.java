

package org.firstinspires.ftc.teamcode;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="GyroNonlinear", group="Demo Bot")
//@Disabled
public class GyroNonlinear extends OpMode {
    final static double MOTOR_POWER = 0.2;
   private int xVal, yVal, zVal;     // Gyro rate Values
   private int heading;              // Gyro integrated heading
   private int angleZ;
    boolean lastResetState = false;
    boolean curResetState  = false;
  public  int resetState = 0, v_state = 0;
    public GyroNonlinear() {}
    ModernRoboticsI2cGyro gyro;
    DcMotor motorRB, motorRF, motorLB, motorLF, spin, shoot;
    public static double TIME_STATE1;
    public double timeAuto = 0;
    public double timeStart = 0;
    public double time1,time2,time3,time4, pos1,pos2,pos3,pos4 = 0;
    public int count = 0;



    public void init() {
        //bPrevState = false;
        //bCurrState = true;
        //bLedOn = true;
        motorRB = hardwareMap.dcMotor.get("motor_1");
        motorRF = hardwareMap.dcMotor.get("motor_2");
        motorLB = hardwareMap.dcMotor.get("motor_3");
        motorLF = hardwareMap.dcMotor.get("motor_4");
        motorLB.setDirection(DcMotor.Direction.REVERSE);
        motorLF.setDirection(DcMotor.Direction.REVERSE);
        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        //colorSensor = hardwareMap.colorSensor.get("line");
        while (true) {
            switch (resetState) {
                case 0:
                    telemetry.addData(">", "Gyro Calibrating. Do Not move!");
                    gyro.calibrate();
                    if (!gyro.isCalibrating()) {
                        resetState++;
                    }
                case 1:
                    telemetry.addData(">", "Gyro Calibrated.  Press Start." + Double.toString(resetState));
            }
        }
    }
    @Override
    public void start() {
        timeAuto = 0;
        timeStart = this.time;
    }

    @Override
    public void loop() {
        timeAuto = this.time - timeStart;
        heading = gyro.getHeading();
        angleZ = gyro.getIntegratedZValue();
        // get the x, y, and z values (rate of change of angle).
        xVal = gyro.rawX();
        yVal = gyro.rawY();
        zVal = gyro.rawZ();
        switch(v_state) {
            case 0:
                if (timeAuto < 1) {
                    motorLB.setPower(.5);
                    motorRB.setPower(.5);
                    motorLF.setPower(.5);
                    motorRF.setPower(.5);
                } else if (80 > heading) {
                    motorRB.setPower(.4);
                    motorLF.setPower(-.4);
                    motorLB.setPower(-.4);
                    motorRF.setPower(.4);
                }
                if (100 < heading) {
                    motorRB.setPower(-.4);
                    motorLF.setPower(.4);
                    motorLB.setPower(.4);
                    motorRF.setPower(-.4);
                }

                if (80 < heading && heading < 100) {
                    motorRB.setPower(0);
                    motorLF.setPower(0);
                    motorRF.setPower(0);
                    motorLB.setPower(0);
                    v_state++;
                    if (count == 0) {
                        time1 = timeAuto;
                        pos1 = heading;
                        count++;
                    }
                }
            case 1:
                if ((timeAuto - time1) > 1 && (timeAuto - time1) < 3) {
                    motorRB.setPower(.5);
                    motorLF.setPower(.5);
                    motorRF.setPower(.5);
                    motorLB.setPower(.5);
                } else if ((timeAuto - time1) > 3) {
                    if (pos1+80 > heading) {
                        motorRB.setPower(.4);
                        motorLF.setPower(-.4);
                        motorLB.setPower(-.4);
                        motorRF.setPower(.4);
                    }
                    if (pos1+100 < heading) {
                        motorRB.setPower(-.4);
                        motorLF.setPower(.4);
                        motorLB.setPower(.4);
                        motorRF.setPower(-.4);
                    }

                    if (160 < heading && heading < 200) {
                        motorRB.setPower(0);
                        motorLF.setPower(0);
                        motorRF.setPower(0);
                        motorLB.setPower(0);
                        v_state++;
                        if (count == 1) {
                            time2 = timeAuto;
                            pos2 = heading;
                            count++;
                        }
                    }
                }
        }

        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("time", "elapsed time: " + Double.toString(timeAuto));
        telemetry.addData("0", "Heading %03d", heading);
        telemetry.addData("1", "Int. Ang. %03d", angleZ);
        telemetry.addData("2", "X av. %03d", xVal);
        telemetry.addData("3", "Y av. %03d", yVal);
        telemetry.addData("4", "Z av. %03d", zVal);
        telemetry.addData("5", "resetState %03d", resetState);
    }

    @Override
    public void stop() {}
}

