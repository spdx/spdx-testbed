# SPDX-FileCopyrightText: SPDX Contributors
#
# SPDX-License-Identifier: Apache-2.0
from datetime import datetime

import click
from license_expression import get_spdx_licensing
from spdx.model.actor import Actor, ActorType
from spdx.model.checksum import Checksum, ChecksumAlgorithm
from spdx.model.document import CreationInfo, Document
from spdx.model.extracted_licensing_info import ExtractedLicensingInfo
from spdx.model.file import File
from spdx.model.package import Package, PackageVerificationCode
from spdx.model.relationship import Relationship, RelationshipType
from spdx.model.snippet import Snippet
from spdx.model.spdx_none import SpdxNone
from spdx.writer.xml.xml_writer import write_document_to_file


@click.command()
@click.option("-target", "-t", help="Path where the generated file will be stored.")
def main(target: str):
    creation_info = CreationInfo(spdx_version="SPDX-2.3", spdx_id="SPDXRef-DOCUMENT", name="document name",
                                 data_license="CC0-1.0", document_namespace="https://some.namespace",
                                 creators=[Actor(ActorType.TOOL, "test-tool")],
                                 created=datetime(2022, 1, 1))
    extracted_licensing_info = [
        ExtractedLicensingInfo(license_id="LicenseRef-1", extracted_text="extracted license text",
                               license_name="extracted license 1",
                               cross_references=["http://extracted.license", "http://see.also"],
                               comment="extracted license info comment"),
        ExtractedLicensingInfo(license_id="LicenseRef-two", license_name="extracted license 2",
                               extracted_text="extracted license text", cross_references=["http://another.license"],
                               comment="extracted license info comment")]
    package = Package(name="package name", spdx_id="SPDXRef-somepackage", files_analyzed=True,
                      download_location=SpdxNone(),
                      verification_code=PackageVerificationCode("d6a770ba38583ed4bb4525bd96e50461655d2758"),
                      license_concluded=get_spdx_licensing().parse(
                          "((LicenseRef-1 WITH u-boot-exception-2.0) OR LicenseRef-two) AND (Aladdin WITH Classpath-exception-2.0)"),
                      license_info_from_files=[get_spdx_licensing().parse("LicenseRef-1 OR LicenseRef-two"),
                                               get_spdx_licensing().parse("Aladdin WITH Classpath-exception-2.0")],
                      license_declared=get_spdx_licensing().parse(
                          "(LicenseRef-1 OR LicenseRef-two) AND (Aladdin WITH Classpath-exception-2.0)"))
    fileA = File(spdx_id="SPDXRef-fileA", name="./package/faa.txt",
                 checksums=[Checksum(ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758")],
                 license_concluded=get_spdx_licensing().parse("LicenseRef-1 OR LicenseRef-two"),
                 license_info_in_file=[get_spdx_licensing().parse("LicenseRef-1"),
                                       get_spdx_licensing().parse("LicenseRef-two")])
    fileB = File(spdx_id="SPDXRef-fileB", name="./package/fbb.txt",
                 checksums=[Checksum(ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758")],
                 license_concluded=get_spdx_licensing().parse("Aladdin WITH Classpath-exception-2.0"),
                 license_info_in_file=[get_spdx_licensing().parse("Aladdin"),
                                       get_spdx_licensing().parse("DL-DE-BY-2.0")])
    snippet = Snippet(spdx_id="SPDXRef-somesnippet", file_spdx_id="SPDXRef-fileB", byte_range=(100, 200),
                      license_concluded=get_spdx_licensing().parse("Aladdin"),
                      license_info_in_snippet=[get_spdx_licensing().parse("Aladdin"),
                                               get_spdx_licensing().parse("DL-DE-BY-2.0")], name="snippet name")
    relationships = [
        Relationship(spdx_element_id="SPDXRef-somepackage", relationship_type=RelationshipType.DESCRIBED_BY,
                     related_spdx_element_id="SPDXRef-DOCUMENT"),
        Relationship(spdx_element_id="SPDXRef-somepackage", relationship_type=RelationshipType.CONTAINS,
                     related_spdx_element_id="SPDXRef-fileA"),
        Relationship(spdx_element_id="SPDXRef-somepackage", relationship_type=RelationshipType.CONTAINS,
                     related_spdx_element_id="SPDXRef-fileB")
    ]
    doc = Document(creation_info, extracted_licensing_info=extracted_licensing_info, packages=[package],
                   files=[fileA, fileB], snippets=[snippet], relationships=relationships)
    write_document_to_file(doc, target)

if __name__ == "__main__":
    main()
