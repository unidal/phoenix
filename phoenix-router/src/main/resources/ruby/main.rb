require_relative 'f5_rules'
require_relative 'url_rewrite_rules'
require 'stringio'

default_pool = "w.51ping.com"

f5_buf = StringIO.new
url_rewrite_buf = StringIO.new
parse_f5 f5_buf, default_pool
parse_url_rewrite url_rewrite_buf

rules_xml = File.new("router-rules.xml","w")

rules_xml << "<router-rules>\n"
rules_xml << f5_buf.string
rules_xml << url_rewrite_buf.string
rules_xml << "</router-rules>"

rules_xml.close
