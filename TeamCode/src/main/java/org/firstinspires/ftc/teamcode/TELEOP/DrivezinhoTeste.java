package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "DrivezinhoTeste")
public class DrivezinhoTeste extends OpMode {

    private DcMotor FrontLeft;
    private DcMotor BackLeft;
    private DcMotor FrontRight;
    private DcMotor BackRight;

    private IMU imu;

    @Override
    public void init() {

        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");

        FrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        BackLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        FrontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        BackRight.setDirection(DcMotorSimple.Direction.FORWARD);

        FrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                )
        );

        imu.initialize(parameters);

        telemetry.addLine("DrivezinhoTeste iniciado");
        telemetry.addLine("OPTIONS = reset heading");
        telemetry.update();
    }

    @Override
    public void loop() {

        if (gamepad1.options) {
            imu.resetYaw();
        }

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        double heading = imu
                .getRobotYawPitchRollAngles()
                .getYaw(AngleUnit.RADIANS);

        double rotX =
                x * Math.cos(-heading)
                        - y * Math.sin(-heading);

        double rotY =
                x * Math.sin(-heading)
                        + y * Math.cos(-heading);

        rotX *= 1.1;

        double denominator =
                Math.max(
                        Math.abs(rotY)
                                + Math.abs(rotX)
                                + Math.abs(rx),
                        1
                );

        double frontLeftPower =
                (rotY + rotX + rx) / denominator;

        double backLeftPower =
                (rotY - rotX + rx) / denominator;

        double frontRightPower =
                (rotY - rotX - rx) / denominator;

        double backRightPower =
                (rotY + rotX - rx) / denominator;

        double speed;

        if (gamepad1.left_bumper) {
            speed = 0.25;
        } else if (gamepad1.right_bumper) {
            speed = 1.0;
        } else {
            speed = 0.6;
        }

        FrontLeft.setPower(frontLeftPower * speed);
        BackLeft.setPower(backLeftPower * speed);
        FrontRight.setPower(frontRightPower * speed);
        BackRight.setPower(backRightPower * speed);

        telemetry.addData("Heading", Math.toDegrees(heading));
        telemetry.addData("Velocidade", speed);

        telemetry.addData("FrontLeft", frontLeftPower);
        telemetry.addData("BackLeft", backLeftPower);
        telemetry.addData("FrontRight", frontRightPower);
        telemetry.addData("BackRight", backRightPower);

        telemetry.addLine("LB = precisão");
        telemetry.addLine("RB = velocidade máxima");
        telemetry.addLine("OPTIONS = reset heading");

        telemetry.update();
    }
}