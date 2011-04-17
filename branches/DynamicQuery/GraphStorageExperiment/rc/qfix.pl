#!/usr/bin/perl

open INFILE, '<' . $ARGV[0];

@lines = <INFILE>;

close INFILE;

for $line(@lines) {
  if ($line =~ /\s*(\S+)\s+(\S+)\s+(\S+)\s+\./) {
    $sub = $1;
    $pred = $2;
    $obj = $3;
    $sub = replace($sub);
    $pred = replace($pred);
    $obj = replace($obj);
    print $sub . " " . $pred . " " . $obj . " .\n";
  }
  else {
    print $line;
  }
}

sub replace {
  $str = shift;
  
  if ($str =~ m/^\?/) {
    $str =~ s/-/_/g;
  }
  return $str;
}
