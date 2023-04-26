	.data
	.globl validate_checksum
	.text

# -- validate_checksum --
# Arguments:
# a0 : Address of a string containing a german IBAN (22 characters)
# Return:
# v0 : the checksum of the IBAN
validate_checksum:
	
	# store address in t7
	move $t7 $a0
	
	# Load character at address a0 to s0
	lb $s0 ($a0)
	#convert A to 10 by substracting 31 from ascii code
	addi $s0 $s0 -31
	# increment address by 1
	addi $a0 $a0 1
	
	# Load second character at a1 to s1
	lb $s1 ($a0)
	# convert A to 10 by substracting 31 from ascii code
	addi $s1 $s1 -31
	# increment address by 1
	addi $a0 $a0 1
	
	# Load second character at a2 to s1
	lb $s2 ($a0)
	addi $s2 $s2 -31
	# increment address by 1
	addi $a0 $a0 1
	
	# Load second character at a3 to s1
	lb $s3 ($a0)
	addi $s3 $s3 -31
	# increment address by 1
	addi $a0 $a0 1


	# s0..s3 now contains the first 4 bytes
	
	
	b copyMem


copyMem:

	# set t0 to 18 since 18 bytes need to be moved
	li $t0 18

	# copy address of a0 to a1
	move $a1 $a0
	
	# reset address of a0 to first address
	subi $a0 $a0 4

	# a0 contains the source address for copy
	# a1 contains the target address for copy


	b .loop

.loop:

	# decrement t0 by 1
	addi $t0 $t0 -1
	
	# check if t0 is 0
	# if so, jump to .done
	beqz $t0 .done
	
	# else move $a1 $a0
	#move $a0 $a1
	sb $a1 ($a0)
	
	# increment a0 by 1
	addi $a0 $a0 1
	
	# increment a1 by 1
	addi $a1 $a1 1
	
	b .loop


.done:


	# sb $s0 ($a0) to store first character as int at addr a0
	sb $s0 ($a0)
	# increment a0 by 1
	addi $a0 $a0 1
	
	# sb $s1 ($a0) to store second character as int at addr a0
	sb $s1 ($a0)
	# increment a0 by 1
	addi $a0 $a0 1
	
	# sb $s2 ($a0) to store third character as int at addr a0
	sb $s2 ($a0)
	# increment a0 by 1
	addi $a0 $a0 1
	
	# sb $s3 ($a0) to store fourth character as int at addr a0
	sb $s0 ($a0)
	



	# call modulo function with a0 as starting address of buffer, a1 with 22, a2 with 97
	
	move $a0 $t7
	li $a1 22
	li $a2 97

	jal modulo_str
	
	jr	$ra
