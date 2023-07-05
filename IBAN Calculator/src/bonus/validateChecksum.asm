	.data
	.globl validate_checksum
	.text


# -- validate_checksum --
# Arguments:
# a0 : Address of a string containing a german IBAN (22 characters)
# Return:
# v0 : the checksum of the IBAN
validate_checksum:
	# Use t0-t9 since they are caller save and we aren't allowed to write to memory
	
	# backup return address
	move $t8 $ra
	# backup a0 parameter
	move $t9 $a0
		
	# --- modulo_str ---
	# Arguments:
	# a0: start address of the buffer
	# a1: number of bytes in the buffer
	# a2: divisor
	# Return:
	# v0: the decimal number (encoded using ASCII digits '0' to '9') in the buffer [$a0 to $a0 + $a1 - 1] modulo $a2 
	
	# Add offset of 4 to base address to skip letters and checksum
	addiu $a0 $a0 4
	# set number of bytes to read to 18 since 22 - 4 = 18
	li $a1 18
	li $a2 97
	
	# calculate modulo of blz and knr
	jal modulo_str
	
	
	# restore base address to
	move $a0 $t9
	
	
	# convert letters at beginning to numbers by A=10,B=11,...,Z=35
	# load first letter into t0
	lb $t0 ($a0)
	addiu $a0 $a0 1
	#load second letter into t1
	lb $t1 ($a0)
	addiu $a0 $a0 1
	
	# substract 55 to get A=10,B=11,...,Z=35
	subi $t0 $t0 55
	subi $t1 $t1 55
	
	# split into left and right digit of first character and store into t2 and t3
	li $a2 10
	rem $t3 $t0 $a2
	div $t2 $t0 $a2
	
	# split into left and right digit of second character and store into t4 and t5
	rem $t5 $t1 $a2
	div $t4 $t1 $a2	
	
	
	# multiply modulo result in v0 with 10000 and add the four digits of the country code to it
	mul $v0 $v0 10000
	
	# multiply t2 with 1000 and add it to mod result
	mul $t2 $t2 1000
	add $v0 $v0 $t2
	
	# multiply t3 with 100 and add it to mod result
	mul $t3 $t3 100
	add $v0 $v0 $t3
	
	# multiply t4 with 10 and add it to mod result
	mul $t4 $t4 10
	add $v0 $v0 $t4
	
	# add t5 to mod result
	add $v0 $v0 $t5
	
	
	# calculate modulo of v0 by 97
	li $a2 97
	rem $v0 $v0 $a2
	# v0 now contains the result of the mod operation of blz, knr and country code
	
	
	# load check digits into t0 and t1
	lb $t0 ($a0)
	addiu $a0 $a0 1
	lb $t1 ($a0)
	
	# convert ascii to decimal
	subiu $t0 $t0 48
	subiu $t1 $t1 48
	
	
	# multiply v0 with 100
	mul $v0 $v0 100
	
	# add t0
	mul $t0 $t0 10
	add $v0 $v0 $t0
	
	# add t1
	add $v0 $v0 $t1
	
	
	# calculate modulo of v0 by 97
	li $a2 97
	rem $v0 $v0 $a2
	
	
	# restore return address
	move $ra $t8
	jr	$ra
