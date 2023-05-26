	.data
	.globl validate_checksum
	.text

# -- validate_checksum --
# Arguments:
# a0 : Address of a string containing a german IBAN (22 characters)
# Return:
# v0 : the checksum of the IBAN
validate_checksum:

	# Callee save
	# store values of s0,s1,s2,s6,s7
	subi $sp $sp 20
	sw $s0 0($sp)
	sw $s1 4($sp)
	sw $s2 8($sp)
	sw $s6 12($sp)
	sw $s7 16($sp)


	# Skip letters in IBAN
	#addi $a0 $a0 2
	lb $t5 ($a0)
	addi $a0 $a0 1
	lb $t6 ($a0)
	addi $a0 $a0 1


	# Store Checksum Digits
	
	# Load second character at a0 to s6
	lb $s6 ($a0)
	addi $s6 $s6 -48
	# increment address by 1
	addi $a0 $a0 1
	
	# Load third character at a0 to s7
	lb $s7 ($a0)
	addi $s7 $s7 -48
	# increment address by 1
	addi $a0 $a0 1


moveMem:

	subi $sp $sp 32
	sw $a0 0($sp)
	sw $a1 4($sp)
	sw $a2 8($sp)
	sw $t7 12($sp)
	sw $t9 16($sp)
	sw $ra 20($sp)
	sw $t5 24($sp)
	sw $t6 28($sp)
	
	# increment source address by offset of 4
	subi $a0 $a0 4
	addi $a1 $a0 4
	li $a2 18

	jal memcpy


	
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t7 12($sp)
	lw $t9 16($sp)
	lw $ra 20($sp)
	lw $t5 24($sp)
	lw $t6 28($sp)
	addi $sp $sp 32

.done:

	# substract offset 4 from a0 to get base address
	subi $a0 $a0 4
	


	#	TODO: DYNAMIC CALCULATION BY DIVISION AND REMAINDER OF COUNTRY CODE
	#	NOTE: USE S0 for storing country code and converting
	#	NOTE: USE S1,S2 for calculation


	# loaded first country code letter to t5

	

	
	# substract 55 from letter to get A=10,B=11,C=12,...,Z=35
	subi $s0 $t5 55

	li $s1 10
	rem $s2 $s0 $s1
	div $s1 $s0 $s1

	# s1 contains first digit of country code letter
	# s2 contains second digit of country code letter

	# convert to ascii
	addi $s1 $s1 48
	addi $s2 $s2 48
	
	# add 18 as offset since last 4 characters are country code as numbers
	addi $a0 $a0 18

	sb $s1 ($a0)
	addi $a0 $a0 1
	sb $s2 ($a0)
	addi $a0 $a0 1

	# loaded second country code letter to s6
	
	# substract 55 from letter to get A=10,B=11,C=12,...,Z=35
	subi $s0 $t6 55

	li $s1 10
	rem $s2 $s0 $s1
	div $s1 $s0 $s1


	# s1 contains first digit of country code letter
	# s2 contains second digit of country code letter
	
	# convert to ascii
	addi $s1 $s1 48
	addi $s2 $s2 48
	
	sb $s1 ($a0)
	addi $a0 $a0 1
	sb $s2 ($a0)


	




	# caller save
	subi $sp $sp 24
	sw $a0 0($sp)
	sw $a1 4($sp)
	sw $a2 8($sp)
	sw $t7 12($sp)
	sw $t9 16($sp)
	sw $ra 20($sp)


	# call modulo function with a0 as starting address of buffer, a1 with 22, a2 with 97
	
	subi $a0 $a0 21
	li $a1 22
	li $a2 97
	


	jal modulo_str

	# caller restore
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t7 12($sp)
	lw $t9 16($sp)
	lw $ra 20($sp)
	addi $sp $sp 24
	
	# v0 contains result of mod operation
	
	# need to multiply v0 with 10 and add first digit of checksum then do mod again
	# multiply result with 10 and add second digit of checksum then do mod again
	
	# set up values for manual mod calculation
	li $s1 10
	li $a2 97

	# calculate modulo manually of the last two numbers (check digits)
	mul $s0 $v0 $s1
	add $v0 $s0 $s6
	rem $v0 $v0 $a2
	
	# calculate modulo manually of the last two numbers (check digits)
	mul $v0 $v0 $s1
	add $v0 $v0 $s7
	rem $v0 $v0 $a2



	# Callee restore
	# restore values of s0,s1,s2,s6,s7
	
	lw $s0 0($sp)
	lw $s1 4($sp)
	lw $s2 8($sp)
	lw $s6 12($sp)
	lw $s7 16($sp)
	addi $sp $sp 20
	
	jr	$ra
