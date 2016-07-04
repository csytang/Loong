################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/compute-accuracy.c \
../src/distance.c \
../src/word-analogy.c \
../src/word2phrase.c \
../src/word2vec.c 

OBJS += \
./src/compute-accuracy.o \
./src/distance.o \
./src/word-analogy.o \
./src/word2phrase.o \
./src/word2vec.o 

C_DEPS += \
./src/compute-accuracy.d \
./src/distance.d \
./src/word-analogy.d \
./src/word2phrase.d \
./src/word2vec.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/include/darwin -I/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/include -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


