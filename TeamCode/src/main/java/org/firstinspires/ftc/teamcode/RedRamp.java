package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.opencv.core.Mat;

import java.util.ArrayList;

import ftc.vision.BeaconColorResult;
import ftc.vision.FrameGrabber;
import ftc.vision.ImageProcessorResult;

@Autonomous(name="RedRamp", group="Demo Bot")
//@Disabled
public class RedRamp extends OpMode {
    FrameGrabber frameGrabber = FtcRobotControllerActivity.frameGrabber; //Get the frameGrabber
        DcMotor motorRB, motorRF, motorLB, motorLF, spin, shoot;
        double timeAuto = 0, timeStart = 0, timeLine = 0, timeColor = 0;
        ArrayList<Double> timeStep = new ArrayList<Double>();
        Servo hold, push;
        byte[] colorCcache;
        I2cDevice colorC;
        I2cDeviceSynch colorCreader;
        BeaconColorResult result;
        boolean sawLine = false;
        ModernRoboticsI2cGyro gyro;
        private int xVal, yVal, zVal;     // Gyro rate Values
        private int heading;              // Gyro integrated heading
        private int angleZ;
        public int resetState;
        int count = 0;
        public int m_count;
        public double LB_Vector,RB_Vector, LF_Vector, RF_Vector, radTarget;

    public void init() {
        motorRF = hardwareMap.dcMotor.get("motor_1");
        motorRB = hardwareMap.dcMotor.get("motor_2");
        motorLB = hardwareMap.dcMotor.get("motor_3");
        motorLF = hardwareMap.dcMotor.get("motor_4");
        motorRB.setDirection(DcMotor.Direction.REVERSE);
        motorRF.setDirection(DcMotor.Direction.REVERSE);
        hold = hardwareMap.servo.get("hold");
        push = hardwareMap.servo.get("push");
        spin = hardwareMap.dcMotor.get("spin");
        shoot = hardwareMap.dcMotor.get("shoot");
        shoot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        colorC = hardwareMap.i2cDevice.get("line");
        colorCreader = new I2cDeviceSynchImpl(colorC, I2cAddr.create8bit(0x3c), false);
        colorCreader.engage();
        colorCreader.write8(3, 0);
        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        switch (resetState) {
            case 0:
                telemetry.addData(">", "Gyro Calibrating. Do Not move!" + resetState);
                gyro.calibrate();
                if (!gyro.isCalibrating()) {
                    resetState++;
                }
            case 1:
                telemetry.addData(">", "Gyro Calibrated.  Press Start.");
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

        //Makes the robot move in a specified direction in a certain ammount of time at a certain speed
        public void movementTarget(double powerTarget, double timeTarget, double angleTarget) {
            if (m_count == 0) {
                timeStep.set(0, timeAuto);
                m_count++;
            }

            if ((timeStep.get(m_count) - timeStep.get(m_count - 1)) == timeTarget) {
                motorLB.setPower(0);
                motorLF.setPower(0);
                motorRB.setPower(0);
                motorRF.setPower(0);
                m_count++;
            } else {
                timeStep.set(m_count, timeAuto);
                motorLB.setPower(powerTarget * LB_Vector);
                motorLF.setPower(powerTarget * LF_Vector);
                motorRB.setPower(powerTarget * RB_Vector);
                motorRF.setPower(powerTarget * RF_Vector);

            }
            //converts angles to radians, which is accepeted by Math.java trig methods
            radTarget = angleTarget*(3.14159265/180);
            LB_Vector = (-Math.sin(radTarget) - Math.cos(radTarget));
            LF_Vector = (-Math.sin(radTarget) + Math.cos(radTarget));
            RB_Vector = (Math.sin(radTarget) + Math.cos(radTarget));
            RF_Vector = (Math.sin(radTarget) - Math.cos(radTarget));
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

