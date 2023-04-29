	.data
	.globl validate_checksum
	.text

# -- validate_checksum --
# Arguments:
# a0 : Address of a string containing a german IBAN (22 characters)
# Return:
# v0 : the checksum of the IBAN
validate_checksum:

	move $t9 $ra
	
	# store address in t7
	move $t7 $a0
	addi $a0 $a0 2
	
	# Load second character at a0 to s6
	lb $s6 ($a0)
	addi $s6 $s6 -48
	# increment address by 1
	addi $a0 $a0 1
	
	# Load second character at a0 to s7
	lb $s7 ($a0)
	addi $s7 $s7 -48
	# increment address by 1
	addi $a0 $a0 1


	# s0 and s1 now contains the two check digits
	
	
	b moveMem


moveMem:

	# copy target address
	move $a0 $t7
	# increment source address by offset of 4
	addi $a1 $a0 4
	li $a2 18

	jal memcpy

	b .done
.done:

	move $a0 $t7
	addi $a0 $a0 18

	# store 1 as ascii at addr a0
	li $s0 49
	sb $s0 ($a0)
	# increment a0 by 1
	addi $a0 $a0 1
	
	# store 3 as ascii at addr a0
	li $s0 51
	sb $s0 ($a0)
	# increment a0 by 1
	addi $a0 $a0 1
	
	# store 1 as ascii at addr a0
	li $s0 49
	sb $s0 ($a0)
	# increment a0 by 1
	addi $a0 $a0 1
	
	# store 4 as ascii at addr a0
	li $s0 52
	sb $s0 ($a0)
	



	# call modulo function with a0 as starting address of buffer, a1 with 22, a2 with 97
	
	move $a0 $t7
	li $a1 22
	li $a2 97

	jal modulo_str
	
	# v0 contains result of mod operation
	
	# need to multiply v0 with 10 and add first digit of checksum then do mod again
	# multiply result with 10 and add second digit of checksum then do mod again
	
	# set up values for manual mod calculation
	li $s1 10
	li $a2 97


	# calculate modulo manually of the last two numbers (check digits)
	mul $s0 $v0 $s1
	add $s0 $s0 $s6
	rem $s0 $s0 $a2
	
	# calculate modulo manually of the last two numbers (check digits)
	mul $s0 $s0 $s1
	add $s0 $s0 $s7
	rem $s0 $s0 $a2
	
	move $v0 $s0
	
	move $ra $t9
	
	jr	$ra
