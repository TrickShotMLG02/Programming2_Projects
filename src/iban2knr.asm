	.data
	.globl iban2knr
	.text
# -- iban2knr
# Arguments:
# a0: IBAN buffer (22 bytes)
# a1: BLZ buffer (8 bytes)
# a2: KNR buffer (10 bytes)
iban2knr:
	
	# copy address from $a0 to $s0
	move $s0 $a0
	move $s1 $a1
	move $s2 $a2
	
	# Grab BLZ
	# Grab substring of a0 from a1 (4) by reading (a2) 8 characters
	li $a1 4
	li $a2 8
	move $a3 $s1
	
	
	# save return address
    	sw $ra, ($sp) 
    	# Jump and link to substring function
	jal substring
	# restore return address		
	lw $ra, ($sp)
	
	
	# Grab KNR
	# Grab substring of a0 from a1 (12) by reading (a2) 10 characters
	move $a0 $s0
	li $a1 12
	li $a2 10
	move $a3 $s2
	
	
	# save return address
    	sw $ra, ($sp)
	# Jump to substring
	jal substring
	# restore return address		
	lw $ra, ($sp)
	
	
	# restore original parameters from s1/s2 to a1/a2
	move $a1 $s1
	move $a2 $s2
	
	# not needed
	jr	$ra
	
	
	
# a0: starting address
# a1: nth character to start at
# a2: characters to read
# a3: target address
#
# Returns: data address in k0
# USING T0...T3 internally!
substring:

	# Copy address to t0 to work on
	move $t0 $a0
	# Copy Index of starting character to t1
	move $t1 $a1
	# Copy amount of characters to read to t2
	move $t2 $a2
	
	move $t3 $a3 
	
	# Add offset to start from to address
	add $t0 $t0 $t1
	
	j .loop
	
.loop:
	# Jump to .done if characters left to read equals 0
	beqz $t2 .done
	
	
	# Else load character at addr from s0 into a0
	lb $a0, ($t0)
	
	# Store byte (current char) at address of t3
	sb $a0 ($t3)
		
	# Increment address by 1 for next character
	addi $t0 $t0 1
	# Decrease amount of characters left for reading by 1
	addi $t2 $t2 -1
	
	# Increment target address by 1
	addi $t3 $t3 1
	
	# Jump to start of loop again
	j .loop
	
.done:
	jr $ra
	

