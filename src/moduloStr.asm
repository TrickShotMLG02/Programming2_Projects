	.data
	.globl modulo_str
	.text

# --- modulo_str ---
# Arguments:
# a0: start address of the buffer
# a1: number of bytes in the buffer
# a2: divisor
# Return:
# v0: the decimal number (encoded using ASCII digits '0' to '9') in the buffer [$a0 to $a0 + $a1 - 1] modulo $a2 
modulo_str:
	# TODO

	# Create backup to local variables s0..s2
	move $s0 $a0
	move $s1 $a1
	move $s2 $a2
	
	b mod


mod:

	# Decrement number of bytes left by one
	addi $s1 $s1 -1
	
	# Else load character at addr from t0 into a0
	lb $s0, ($a0)

	# substract ascii offset of number 0
	subi $s0 $s0 48
	
	# declare constant value 10 in t3
	li $t3 10

	# multiply content of last modulo operation (t1) by factor 10 (t3) and store in t0
	mul $t0 $t1 $t3
	# calculate mod of new number s0 and divisor s2
	rem $t1 $s0 $s2
	# add result of previous modulo operation t1 and the result of the multiplication of t0 in t1
	add $t1 $t1 $t0
	# calculate new mod of the value from the addition before and store to t1
	rem $t1 $t1 $s2

	# increment buffer address by 1
	addi $a0 $a0 1

	# check if there are no bytes left to be read
	beqz $s1 .done

	# else loop to mod
	b mod

.done:

	# Copy value (result of calculation) from t1 to v0 for return
	move $v0 $t1
	# Return to other function
	jr $ra
