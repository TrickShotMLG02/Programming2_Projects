	.data
	.globl knr2iban
	
	ibanModBuf:
		.space 24
	.text
# -- knr2iban
# Arguments:
# a0: IBAN buffer (22 bytes)
# a1: BLZ buffer (8 bytes)
# a2: KNR buffer (10 bytes)
knr2iban:

	subi $sp $sp 12
	sw $s0 0($sp)
	sw $s1 4($sp)
	sw $s2 8($sp)



	subi $sp $sp 32
	sw $a0 0($sp)
	sw $a1 4($sp)
	sw $a2 8($sp)
	sw $t6 12($sp)
	sw $t7 16($sp)
	sw $t8 20($sp)
	sw $t9 24($sp)
	sw $ra 28($sp)
	
	
	# copy blz to ibanModBuf
	la $a0 ibanModBuf
	li $a2 8
	
	jal memcpy
	

	# restore stack registers
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t6 12($sp)
	lw $t7 16($sp)
	lw $t8 20($sp)
	lw $t9 24($sp)
	lw $ra 28($sp)

	
	
	# copy ktr to ibanModBuf
	la $a0 ibanModBuf
	addi $a0 $a0 8
	move $a1 $a2
	li $a2 10
	
	jal memcpy



	# restore stack registers
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t6 12($sp)
	lw $t7 16($sp)
	lw $t8 20($sp)
	lw $t9 24($sp)
	lw $ra 28($sp)

	
	# store country code to ibanModBuf
	la $a0 ibanModBuf
	addi $a0 $a0 18
	li $a1 49
	sb $a1 ($a0)
	addi $a0 $a0 1
	li $a1 51
	sb $a1 ($a0)
	addi $a0 $a0 1
	li $a1 49
	sb $a1 ($a0)
	addi $a0 $a0 1
	li $a1 52
	sb $a1 ($a0)
	addi $a0 $a0 1
	li $a1 48
	sb $a1 ($a0)
	addi $a0 $a0 1
	li $a1 48
	sb $a1 ($a0)
	addi $a0 $a0 1
	


	# restore stack registers
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t6 12($sp)
	lw $t7 16($sp)
	lw $t8 20($sp)
	lw $t9 24($sp)
	lw $ra 28($sp)


	
	# calculate modulo of ibanModBuf
	la $a0 ibanModBuf
	li $a1 24
	li $a2 97
	
	move $t7 $s0
	move $t6 $s2
	move $t8 $s1
	jal modulo_str
	move $s0 $t7
	move $s2 $t6
	move $s1 $t8



	# restore stack registers
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t6 12($sp)
	lw $t7 16($sp)
	lw $t8 20($sp)
	lw $t9 24($sp)
	lw $ra 28($sp)
	
	# store DE in IBAN Buffer
	li $a1 68
	sb $a1 ($a0)
	addi $a0 $a0 1
	
	li $a1 69
	sb $a1 ($a0)
	addi $a0 $a0 1



	move $a1 $a0
	# calculate check digits by substracting result from 98
	li $t7 98
	sub $a0 $t7 $v0
	# store address in a1 and add offset of 2 to skip DE part
	
	#addi $a1 $a1 2
	# set number of bytes to 2
	li $a2 2
	
	# convert integer to ascii code
	jal int_to_buf	



	# restore stack registers
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t6 12($sp)
	lw $t7 16($sp)
	lw $t8 20($sp)
	lw $t9 24($sp)
	lw $ra 28($sp)

	
	
	# copy blz to IBAN Buffer
	addi $a0 $a0 4
	li $a2 8
	jal memcpy



	# restore stack registers
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t6 12($sp)
	lw $t7 16($sp)
	lw $t8 20($sp)
	lw $t9 24($sp)
	lw $ra 28($sp)


	
	# copy ktr to IBAN Buffer
	addi $a0 $a0 12
	move $a1 $a2
	li $a2 10
	jal memcpy
	


	# restore stack registers
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $t6 12($sp)
	lw $t7 16($sp)
	lw $t8 20($sp)
	lw $t9 24($sp)
	lw $ra 28($sp)
	addi $sp $sp 32
	

	lw $s0 0($sp)
	lw $s1 4($sp)
	lw $s2 8($sp)
	addi $sp $sp 12

	jr	$ra
