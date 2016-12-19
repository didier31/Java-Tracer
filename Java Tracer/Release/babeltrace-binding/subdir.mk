################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../babeltrace-binding/ctfwriter_wrap.c 

OBJS += \
./babeltrace-binding/ctfwriter_wrap.o 

C_DEPS += \
./babeltrace-binding/ctfwriter_wrap.d 


# Each subdirectory must supply rules for building sources it contributes
babeltrace-binding/%.o: ../babeltrace-binding/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C Compiler'
	gcc -I"C:\Program Files\Java\jdk1.8.0_102\include" -I"C:\Users\didier\git\babeltrace\include" -I"C:\Program Files\Java\jdk1.8.0_102\include\win32" -O3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


