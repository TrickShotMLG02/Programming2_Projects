	.data
	.globl iban2knr
	.text
# -- iban2knr
# Arguments:
# a0: IBAN buffer (22 bytes)
# a1: BLZ buffer (8 bytes)
# a2: KNR buffer (10 bytes)
iban2knr:

	# Callee save
	# store values of s0,s1,s2
	subi $sp $sp 12
	sw $s0 0($sp)
	sw $s1 4($sp)
	sw $s2 8($sp)
	

	# Caller Save
	# store a0,a1,a2,a3,ra,t0 to stack
	subi $sp $sp 24
	sw $a0 0($sp)
	sw $a1 4($sp)
	sw $a2 8($sp)
	sw $a3 12($sp)
	sw $t0 16($sp)
	sw $ra 20($sp)


	# Grab BLZ
	move $a3 $a1
	li $a1 4
	li $a2 8
	
	# Jump to substring
	jal substring

	# Caller Restore
	# restore a0,a1,a2,a3,ra,t0 to stack
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $a3 12($sp)
	lw $t0 16($sp)
	lw $ra 20($sp)

	# leave stack untouched, since otherwise i would need to store registers again while
	# not changing any values in them, thus skip # addi $sp $sp 24


	# Grab KNR
	move $a3 $a2
	li $a1 12
	li $a2 10
	
	# Jump to substring
	jal substring


	# Caller restore
	# restore a0,a1,a2,a3,ra,t0 to stack
	lw $a0 0($sp)
	lw $a1 4($sp)
	lw $a2 8($sp)
	lw $a3 12($sp)
	lw $t0 16($sp)
	lw $ra 20($sp)
	addi $sp $sp 24



	# Callee Restore
	# restore values of s0,s1,s2
	lw $s0 0($sp)
	lw $s1 4($sp)
	lw $s2 8($sp)
	addi $sp $sp 12	

	
	jr	$ra
	
	
	
# a0: starting address
# a1: nth character to start at
# a2: characters to read
# a3: target address
#
# Returns: data address in k0
# USING T0 internally!
substring:
	
	# Add offset to start from to address
	add $a0 $a0 $a1
	
	j .loop
	
.loop:
	# Jump to .done if characters left to read equals 0
	beqz $a2 .done
	
	
	# Else load character at addr from s0 into a0
	lb $t0, ($a0)
	
	# Store byte (current char) at address of t3
	sb $t0 ($a3)
		
	# Increment address by 1 for next character
	addi $a0 $a0 1
	# Decrease amount of characters left for reading by 1
	addi $a2 $a2 -1
	
	# Increment target address by 1
	addi $a3 $a3 1
	
	# Jump to start of loop again
	j .loop
	
.done:

	jr $ra
	

