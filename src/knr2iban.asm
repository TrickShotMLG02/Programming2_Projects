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
	# TODO
	
	move $s0 $a0
	move $s1 $a1
	move $s2 $a2
	
	# save ra address
	move $t9 $ra
	
	# copy blz to ibanModBuf
	la $a0 ibanModBuf
	#move $a1 $a1
	li $a2 8
	
	jal memcpy
	
	
	# copy ktr to ibanModBuf
	la $a0 ibanModBuf
	addi $a0 $a0 8	
	move $a1 $s2
	li $a2 10
	
	jal memcpy
	
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
	
	
	# calculate modulo of ibanModBuf
	la $a0 ibanModBuf
	li $a1 24
	li $a2 97
	
	
	move $t7 $s0
	move $t6 $s2
	jal modulo_str
	move $s0 $t7
	move $s2 $t6
	
	# TODO: FIX THIS PIECE OF SHIT
	
	
	
	# store DE in IBAN Buffer
	move $a0 $s0
	li $a1 68
	sb $a1 ($a0)
	addi $a0 $a0 1
	
	li $a1 69
	sb $a1 ($a0)
	addi $a0 $a0 1
	
	
	#substract mod result (a6) from 98. Result of operation is checknum
	li $a1 98
	
	# can be deleted
	li $a1 120
	
	
	sub $s6 $a1 $s6
	sb $s6 ($a0)
	addi $a0 $a0 1
	
	# can be deleted
	sb $s6 ($a0)
	
	
	#
	# TODO
	#
	
	
	
	# copy blz to IBAN Buffer
	move $a0 $s0
	addi $a0 $a0 4
	move $a1 $s2
	li $a2 8
	
	jal memcpy
	
	
	# copy ktr to IBAN Buffer
	move $a0 $s0
	addi $a0 $a0 12
	move $a1 $s2
	li $a2 10
	
	jal memcpy
	
	
	
	# Restore ra address
	move $ra $t9
	jr	$ra
